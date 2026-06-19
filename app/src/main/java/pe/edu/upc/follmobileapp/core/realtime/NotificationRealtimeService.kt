package pe.edu.upc.follmobileapp.core.realtime

import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pe.edu.upc.follmobileapp.core.di.NetworkModule
import pe.edu.upc.follmobileapp.features.care.domain.repository.PatientRepository
import pe.edu.upc.follmobileapp.features.emergency.data.local.dao.FallEventDao
import pe.edu.upc.follmobileapp.features.emergency.data.local.models.FallEventEntity
import pe.edu.upc.follmobileapp.features.iam.data.local.AuthLocalDataSource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Cliente de SignalR que mantiene viva la conexión en tiempo real con el backend.
 *
 * El backend (NotificationCommunication) ya decide a QUÉ usuarios envía cada
 * mensaje: agrega cada conexión al grupo "user:{userId}" y solo emite hacia los
 * cuidadores (principales o secundarios) del paciente afectado. Por eso aquí
 * basta con conectarnos con el JWT del usuario logueado; recibiremos únicamente
 * los eventos que nos corresponden.
 *
 * Estrategia de actualización en tiempo real:
 *  - Toda notificación entrante (`notification.created`) se persiste en la tabla
 *    Room `fall_events`. Como TODA la UI (Inicio, Mis Abuelitos, Alertas,
 *    Registro de Caídas) observa flows de Room, las vistas se refrescan solas.
 *  - Para eventos de estado del dispositivo (batería baja/recuperada, des/reconexión)
 *    además se re-sincronizan los pacientes para refrescar batería y estado online
 *    de la tarjeta correspondiente.
 */
class NotificationRealtimeService(
    private val fallEventDao: FallEventDao,
    private val patientRepository: PatientRepository,
    private val authLocalDataSource: AuthLocalDataSource
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var connection: HubConnection? = null

    @Volatile
    private var currentToken: String? = null

    private var connectJob: Job? = null

    /**
     * Inicia (o reinicia si cambió el token) la conexión en tiempo real.
     * Es idempotente: si ya estamos conectados con el mismo token no hace nada.
     */
    @Synchronized
    fun start(token: String) {
        if (token.isBlank()) return

        val alreadyConnected = currentToken == token &&
            connection?.connectionState == HubConnectionState.CONNECTED
        if (alreadyConnected) return

        stopInternal()
        currentToken = token

        val hubUrl = NetworkModule.BASE_URL.trimEnd('/') + "/hubs/notifications"
        val hub = HubConnectionBuilder.create(hubUrl)
            .withAccessTokenProvider(Single.defer { Single.just(token) })
            .build()

        hub.on(EVENT_NOTIFICATION_CREATED, { message ->
            handleNotification(message)
        }, RealtimeNotificationDto::class.java)

        hub.on(EVENT_DEVICE_TELEMETRY, { telemetry ->
            handleTelemetry(telemetry)
        }, DeviceTelemetryRealtimeDto::class.java)

        hub.on(EVENT_INCIDENT_RESOLVED, { resolved ->
            handleIncidentResolved(resolved)
        }, IncidentResolvedRealtimeDto::class.java)

        hub.onClosed { error ->
            if (error != null) {
                Log.w(TAG, "Conexión SignalR cerrada: ${error.message}")
            }
            // Reintentar mientras siga habiendo sesión activa.
            if (currentToken != null) scheduleReconnect()
        }

        connection = hub
        connectInternal()
    }

    /** Detiene la conexión y cancela cualquier reintento. */
    @Synchronized
    fun stop() {
        currentToken = null
        stopInternal()
    }

    private fun stopInternal() {
        connectJob?.cancel()
        connectJob = null
        val hub = connection
        connection = null
        if (hub != null) {
            scope.launch {
                try {
                    if (hub.connectionState != HubConnectionState.DISCONNECTED) {
                        hub.stop().blockingAwait()
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun connectInternal() {
        connectJob?.cancel()
        connectJob = scope.launch {
            while (isActive && currentToken != null) {
                val hub = connection ?: return@launch
                try {
                    if (hub.connectionState == HubConnectionState.DISCONNECTED) {
                        hub.start().blockingAwait()
                        Log.i(TAG, "SignalR conectado a ${NetworkModule.BASE_URL}")
                    }
                    return@launch
                } catch (e: Exception) {
                    Log.w(TAG, "Fallo al conectar SignalR, reintentando en ${RETRY_DELAY_MS}ms: ${e.message}")
                    delay(RETRY_DELAY_MS)
                }
            }
        }
    }

    private fun scheduleReconnect() {
        scope.launch {
            delay(RECONNECT_DELAY_MS)
            if (currentToken != null) connectInternal()
        }
    }

    private fun handleNotification(dto: RealtimeNotificationDto) {
        scope.launch {
            try {
                // 1) Persistir preservando acknowledgedAt si ya fue atendida localmente.
                //    REPLACE ciego borraba el estado "atendido" al re-llegar la misma notificación.
                upsertNotificationPreservingAcknowledged(dto.toEntity())

                // 2) Si cambió el estado del dispositivo, refrescar batería/online.
                if (dto.isDeviceStatusEvent()) {
                    val userId = authLocalDataSource.getLoggedInUser()?.userId
                    if (userId != null) {
                        patientRepository.syncPatients(userId)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error procesando notificación en tiempo real: ${e.message}")
            }
        }
    }

    /** Inserta o reemplaza sin perder acknowledgedAt local (evita que vuelva la alerta como activa). */
    private suspend fun upsertNotificationPreservingAcknowledged(entity: FallEventEntity) {
        val existing = fallEventDao.getById(entity.notificationLogId)
        val merged = if (
            existing != null &&
            !existing.acknowledgedAt.isNullOrBlank() &&
            entity.acknowledgedAt.isNullOrBlank()
        ) {
            entity.copy(acknowledgedAt = existing.acknowledgedAt)
        } else {
            entity
        }
        fallEventDao.saveAlerts(listOf(merged))
    }

    private fun handleIncidentResolved(dto: IncidentResolvedRealtimeDto) {
        if (dto.patientId <= 0) return
        scope.launch {
            try {
                // 1) Quitar la alerta activa de TODAS las vistas (Room es reactivo).
                val nowIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(Date())
                fallEventDao.acknowledgeFallsByPatient(dto.patientId, nowIso)

                // 2) Resolver nombre del paciente para el aviso.
                val patient = patientRepository.getPatientsFlow().first()
                    .firstOrNull { it.id == dto.patientId }
                val patientName = patient
                    ?.let { "${it.firstName} ${it.lastName}".trim() }
                    ?.ifBlank { null }
                    ?: "el paciente"

                // 3) ¿Fui yo quien atendió? (para no mostrarme el banner a mí mismo)
                val currentUserId = authLocalDataSource.getLoggedInUser()?.userId?.toLong()
                val byMe = dto.closedByUserId != null && dto.closedByUserId == currentUserId

                val isFalseAlarm = dto.cancellationReason?.contains("False", ignoreCase = true) == true ||
                    (dto.closedByUserId == null && dto.status.equals("Cancelled", ignoreCase = true))

                val resolvedByName = dto.closedByName?.ifBlank { null }
                    ?: if (dto.closedByUserId == null) "el sistema" else "otro cuidador"

                // 4) Emitir el aviso en vivo para el banner global.
                RealtimeUiEvents.emitIncidentResolved(
                    IncidentResolvedUiEvent(
                        patientName = patientName,
                        resolvedByName = resolvedByName,
                        byMe = byMe,
                        isFalseAlarm = isFalseAlarm
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error procesando incidente resuelto en tiempo real: ${e.message}")
            }
        }
    }

    private fun handleTelemetry(dto: DeviceTelemetryRealtimeDto) {
        if (dto.patientId <= 0) return
        scope.launch {
            try {
                // Actualiza la batería/estado del dispositivo DIRECTO en Room (sin HTTP).
                // La UI (Mis Abuelitos) observa el flow de Room y se refresca al instante.
                patientRepository.updateDeviceTelemetry(
                    patientId = dto.patientId,
                    batteryLevel = dto.batteryLevel,
                    isCharging = dto.isCharging,
                    isOnline = dto.isOnline,
                    lastHeartbeatAt = dto.lastHeartbeatAt
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error procesando telemetría en tiempo real: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "FollRealtime"
        private const val EVENT_NOTIFICATION_CREATED = "notification.created"
        private const val EVENT_DEVICE_TELEMETRY = "device.telemetry"
        private const val EVENT_INCIDENT_RESOLVED = "incident.resolved"
        private const val RETRY_DELAY_MS = 5_000L
        private const val RECONNECT_DELAY_MS = 3_000L
    }
}
