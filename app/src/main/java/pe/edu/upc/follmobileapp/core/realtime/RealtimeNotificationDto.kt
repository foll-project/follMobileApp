package pe.edu.upc.follmobileapp.core.realtime

import pe.edu.upc.follmobileapp.features.emergency.data.local.models.FallEventEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Espejo del payload `NotificationCreatedRealtimeMessage` que emite el backend
 * por SignalR en el evento "notification.created". Los nombres de los campos
 * coinciden EXACTAMENTE con el JSON (camelCase) para que Gson los deserialice.
 *
 * Todos los campos tienen valores por defecto para que Gson pueda instanciar
 * la clase aunque el servidor omita algún atributo nulo.
 */
data class RealtimeNotificationDto(
    val notificationLogId: Long = 0L,
    val userId: Long = 0L,
    val notificationType: String? = null,
    val notificationChannel: String? = null,
    val notificationStatus: String? = null,
    val title: String? = null,
    val body: String? = null,
    val dataJson: String? = null,
    val patientId: Long? = null,
    val deviceId: Long? = null,
    val createdAt: String? = null
) {
    /** Indica si la notificación corresponde a un cambio de estado del dispositivo. */
    fun isDeviceStatusEvent(): Boolean = when (notificationType) {
        "LowBattery", "BatteryRecovered", "DeviceDisconnected", "DeviceReconnected" -> true
        else -> false
    }

    /** Indica si la notificación corresponde a una caída detectada. */
    fun isFallEvent(): Boolean = notificationType == "FallDetected"

    fun toEntity(): FallEventEntity {
        val nowIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())

        return FallEventEntity(
            notificationLogId = notificationLogId,
            userId = userId.toInt(),
            notificationType = notificationType ?: "",
            notificationChannel = notificationChannel ?: "",
            notificationStatus = notificationStatus ?: "",
            title = title ?: "",
            body = body ?: "",
            dataJson = dataJson,
            providerMessageId = null,
            errorMessage = null,
            patientId = patientId,
            deviceId = deviceId?.toInt(),
            sentAt = null,
            readAt = null,
            acknowledgedAt = null,
            createdAt = createdAt ?: nowIso,
            updatedAt = null
        )
    }
}
