package pe.edu.upc.follmobileapp.features.emergency.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.json.JSONObject
import pe.edu.upc.follmobileapp.features.care.data.local.dao.PatientDao
import pe.edu.upc.follmobileapp.features.emergency.data.local.dao.FallEventDao
import pe.edu.upc.follmobileapp.features.emergency.data.local.models.FallEventEntity
import pe.edu.upc.follmobileapp.features.emergency.data.remote.models.PushTokenRequest
import pe.edu.upc.follmobileapp.features.emergency.data.remote.models.ResolveIncidentRequest
import pe.edu.upc.follmobileapp.features.emergency.data.remote.services.EmergencyApiService
import retrofit2.HttpException
import pe.edu.upc.follmobileapp.features.emergency.domain.models.EmergencyAlert
import pe.edu.upc.follmobileapp.features.emergency.domain.models.FallIncident
import pe.edu.upc.follmobileapp.features.emergency.domain.repository.EmergencyRepository
import java.text.SimpleDateFormat
import java.util.*

class EmergencyRepositoryImpl(
    private val fallEventDao: FallEventDao,
    private val patientDao: PatientDao,
    private val apiService: EmergencyApiService
) : EmergencyRepository {

    override fun getAlertsFlow(): Flow<List<EmergencyAlert>> {
        return combine(
            fallEventDao.getAlertsFlow(),
            patientDao.getPatientsFlow()
        ) { events, patients ->
            val patientMap = patients.associateBy { it.id }
            events
                .filter { it.notificationType == "FallDetected" && it.acknowledgedAt.isNullOrBlank() }
                .map { event ->
                    val patient = event.patientId?.let { patientMap[it] }
                    val parsed = parseFallData(event.dataJson)
                    val elapsed = calculateElapsedMinutes(event.createdAt)
                    EmergencyAlert(
                        id = event.notificationLogId,
                        patientId = event.patientId ?: 0L,
                        patientName = patient?.let { "${it.firstName} ${it.lastName}" } ?: "Paciente Desconocido",
                        fallType = parsed.fallType ?: "Caída Detectada",
                        elapsedMinutes = elapsed,
                        address = parsed.location ?: "Dirección no disponible",
                        latitude = parsed.latitude,
                        longitude = parsed.longitude,
                        bloodType = patient?.bloodType ?: "Desconocido",
                        age = patient?.birthDate?.let { calcularEdad(it) } ?: 0,
                        medicalConditions = patient?.illnesses?.joinToString(", ") ?: "Ninguna",
                        medications = patient?.medications?.joinToString(", ") ?: "Ninguno",
                        dni = patient?.dni ?: ""
                    )
                }
        }
    }

    override fun getIncidentsFlow(): Flow<List<FallIncident>> {
        return combine(
            fallEventDao.getAlertsFlow(),
            patientDao.getPatientsFlow()
        ) { events, patients ->
            val patientMap = patients.associateBy { it.id }
            val targetTypes = listOf("FallDetected", "FallCancelled", "FallDismissed", "FalsePositive")
            events
                .filter { targetTypes.contains(it.notificationType) }
                .map { event ->
                    val patient = event.patientId?.let { patientMap[it] }
                    val parsed = parseFallData(event.dataJson)
                    val isReal = event.notificationType == "FallDetected"
                    val (dateStr, timeStr) = formatDateTime(event.createdAt)
                    FallIncident(
                        id = event.notificationLogId,
                        patientId = event.patientId ?: 0L,
                        patientName = patient?.let { "${it.firstName} ${it.lastName}" } ?: "Paciente Desconocido",
                        isRealEmergency = isReal,
                        dateString = dateStr,
                        timeString = timeStr,
                        fallType = parsed.fallType ?: "UNKNOWN",
                        responseTime = if (isReal && !event.acknowledgedAt.isNullOrBlank()) {
                            calculateResponseTime(event.createdAt, event.acknowledgedAt)
                        } else "",
                        observations = event.body,
                        latitude = parsed.latitude,
                        longitude = parsed.longitude
                    )
                }
        }
    }

    override suspend fun syncAlerts(): Result<Unit> = runCatching {
        val localById = fallEventDao.getAllOnce().associateBy { it.notificationLogId }
        val remoteAlerts = apiService.getNotifications()

        val entities = remoteAlerts.map { dto ->
            val local = localById[dto.notificationLogId ?: 0L]
            FallEventEntity(
                notificationLogId = dto.notificationLogId ?: 0L,
                userId = dto.userId ?: 0,
                notificationType = dto.notificationType ?: "",
                notificationChannel = dto.notificationChannel ?: "",
                notificationStatus = dto.notificationStatus ?: "",
                title = dto.title ?: "",
                body = dto.body ?: "",
                dataJson = dto.dataJson,
                providerMessageId = dto.providerMessageId,
                errorMessage = dto.errorMessage,
                patientId = dto.patientId,
                deviceId = dto.deviceId,
                sentAt = dto.sentAt,
                readAt = dto.readAt,
                acknowledgedAt = mergeAcknowledgedAt(dto.acknowledgedAt, local?.acknowledgedAt),
                createdAt = dto.createdAt ?: "",
                updatedAt = dto.updatedAt
            )
        }
        fallEventDao.saveAlerts(entities)

        // Si el incidente ya fue cerrado (atendido en web/mobile/otro cuidador) pero la
        // notificación remota sigue sin acknowledgedAt, alineamos el estado local.
        reconcileAttendedFallsWithBackend()
    }

    override suspend fun acknowledgeAlert(notificationId: Long): Result<Unit> = runCatching {
        apiService.acknowledgeNotification(notificationId)
        fallEventDao.updateAcknowledge(notificationId, currentUtcIso())
    }

    override suspend fun attendFall(patientId: Long): Result<Unit> = runCatching {
        // 1) Buscar el incidente activo del paciente. Si ya no hay (404), alguien más lo atendió.
        val activeIncident = try {
            apiService.getActiveIncident(patientId)
        } catch (e: HttpException) {
            if (e.code() == 404) null else throw e
        }

        // 2) Cerrar el incidente (Resolved). Esto dispara el aviso en tiempo real a los demás.
        //    Si otro cuidador lo cerró justo antes (404/400), lo tratamos como "ya atendida".
        if (activeIncident != null && activeIncident.incidentId > 0) {
            try {
                apiService.resolveIncident(
                    activeIncident.incidentId,
                    ResolveIncidentRequest("Atendido desde la app móvil")
                )
            } catch (e: HttpException) {
                if (e.code() != 404 && e.code() != 400) throw e
            }
        }

        // 3) Confirmar en el backend las notificaciones de caída (para que el próximo sync
        //    no las traiga otra vez como "activas"). Resolver el incidente NO marca la notificación.
        acknowledgeRemoteNotificationsForPatient(patientId)

        // 4) Marcar localmente como atendidas para que la alerta desaparezca al instante.
        markFallsAttended(patientId)
    }

    override suspend fun markPatientFallsAttendedLocally(patientId: Long): Result<Unit> = runCatching {
        markFallsAttended(patientId)
    }

    private suspend fun markFallsAttended(patientId: Long) {
        val nowIso = currentUtcIso()
        fallEventDao.acknowledgeFallsByPatient(patientId, nowIso)
    }

    /**
     * Marca en el servidor las notificaciones FallDetected sin confirmar de un paciente.
     * Best-effort: si otra sesión ya las confirmó, ignoramos 400/404.
     */
    private suspend fun acknowledgeRemoteNotificationsForPatient(patientId: Long) {
        val pending = fallEventDao.getUnacknowledgedFallsByPatient(patientId)
        pending.forEach { event ->
            try {
                apiService.acknowledgeNotification(event.notificationLogId)
            } catch (e: HttpException) {
                if (e.code() != 400 && e.code() != 404) throw e
            }
        }
    }

    /**
     * Tras sincronizar, si un paciente tiene caídas "activas" en Room pero el backend
     * ya no tiene incidente abierto, las marcamos como atendidas (p. ej. atendió otro
     * cuidador o se cerró desde la web).
     */
    private suspend fun reconcileAttendedFallsWithBackend() {
        val patientIds = fallEventDao.getPatientIdsWithUnacknowledgedFalls()
        for (patientId in patientIds) {
            val hasActiveIncident = try {
                apiService.getActiveIncident(patientId)
                true
            } catch (e: HttpException) {
                if (e.code() == 404) false else continue
            }

            if (!hasActiveIncident) {
                acknowledgeRemoteNotificationsForPatient(patientId)
                markFallsAttended(patientId)
            }
        }
    }

    /** Conserva acknowledgedAt local si el remoto aún no lo trae (evita regresiones tras sync). */
    private fun mergeAcknowledgedAt(remote: String?, local: String?): String? {
        return remote?.takeIf { it.isNotBlank() } ?: local?.takeIf { it.isNotBlank() }
    }

    private fun currentUtcIso(): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())
    }

    override suspend fun registerPushToken(token: String): Result<Unit> = runCatching {
        apiService.registerPushToken(PushTokenRequest(token))
    }

    override suspend fun saveObservations(incidentId: Long, observations: String): Result<Unit> = runCatching {
        // En esta versión el backend no provee un endpoint directo para editar observaciones,
        // por lo que se persisten y actualizan de forma local e instantánea en el dispositivo.
        fallEventDao.updateObservations(incidentId, observations)
    }

    private data class ParsedFallData(
        val location: String?,
        val fallType: String?,
        val latitude: Double,
        val longitude: Double
    )

    private fun parseFallData(dataJson: String?): ParsedFallData {
        if (dataJson.isNullOrBlank()) return ParsedFallData(null, null, 0.0, 0.0)
        return try {
            val json = JSONObject(dataJson)
            val lat = json.optDouble("latitude", 0.0)
            val lng = json.optDouble("longitude", 0.0)
            var location = json.optString("location", "").ifBlank { json.optString("address", "") }
            if (location.isBlank() && lat != 0.0 && lng != 0.0) {
                location = "Lat $lat, Lng $lng"
            }
            val fallType = json.optString("fallType", "").ifBlank {
                json.optString("type", "").ifBlank {
                    json.optString("category", "")
                }
            }
            ParsedFallData(
                location = location.ifBlank { null },
                fallType = fallType.ifBlank { null },
                latitude = lat,
                longitude = lng
            )
        } catch (e: Exception) {
            ParsedFallData(null, null, 0.0, 0.0)
        }
    }

    private fun calculateElapsedMinutes(createdAtStr: String): Int {
        val parsedDate = parseIsoDate(createdAtStr) ?: return 0
        val diffMs = System.currentTimeMillis() - parsedDate.time
        val diffMin = diffMs / 60000
        return diffMin.toInt().coerceAtLeast(0)
    }

    private fun formatDateTime(createdAtStr: String): Pair<String, String> {
        val parsedDate = parseIsoDate(createdAtStr) ?: return Pair("--", "--")
        val isToday = android.text.format.DateUtils.isToday(parsedDate.time)
        val dateStr = if (isToday) {
            "Hoy"
        } else {
            SimpleDateFormat("dd MMM", Locale.getDefault()).format(parsedDate)
        }
        val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(parsedDate)
        return Pair(dateStr, timeStr)
    }

    private fun calculateResponseTime(createdAtStr: String, acknowledgedAtStr: String): String {
        val start = parseIsoDate(createdAtStr) ?: return ""
        val end = parseIsoDate(acknowledgedAtStr) ?: return ""
        val diffSec = (end.time - start.time) / 1000
        if (diffSec < 0) return ""
        val min = diffSec / 60
        val sec = diffSec % 60
        return if (min > 0) "${min} min ${sec} seg" else "${sec} seg"
    }

    private fun parseIsoDate(dateStr: String): Date? {
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss"
        )
        for (fmt in formats) {
            try {
                val sdf = SimpleDateFormat(fmt, Locale.US)
                if (fmt.contains("Z")) {
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                }
                val date = sdf.parse(dateStr)
                if (date != null) return date
            } catch (e: Exception) {}
        }
        return null
    }

    private fun calcularEdad(birthDateStr: String): Int {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val birthDate = sdf.parse(birthDateStr) ?: return 0
            val birthCalendar = Calendar.getInstance().apply { time = birthDate }
            val todayCalendar = Calendar.getInstance()
            var age = todayCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
            if (todayCalendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age.coerceAtLeast(0)
        } catch (e: Exception) {
            0
        }
    }
}
