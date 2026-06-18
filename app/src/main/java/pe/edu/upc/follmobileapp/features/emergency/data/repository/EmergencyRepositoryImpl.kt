package pe.edu.upc.follmobileapp.features.emergency.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.json.JSONObject
import pe.edu.upc.follmobileapp.features.care.data.local.dao.PatientDao
import pe.edu.upc.follmobileapp.features.emergency.data.local.dao.FallEventDao
import pe.edu.upc.follmobileapp.features.emergency.data.local.models.FallEventEntity
import pe.edu.upc.follmobileapp.features.emergency.data.remote.models.PushTokenRequest
import pe.edu.upc.follmobileapp.features.emergency.data.remote.services.EmergencyApiService
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
        val remoteAlerts = apiService.getNotifications()
        val entities = remoteAlerts.map { dto ->
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
                acknowledgedAt = dto.acknowledgedAt,
                createdAt = dto.createdAt ?: "",
                updatedAt = dto.updatedAt
            )
        }
        fallEventDao.saveAlerts(entities)
    }

    override suspend fun acknowledgeAlert(notificationId: Long): Result<Unit> = runCatching {
        apiService.acknowledgeNotification(notificationId)
        val nowIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())
        fallEventDao.updateAcknowledge(notificationId, nowIso)
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
