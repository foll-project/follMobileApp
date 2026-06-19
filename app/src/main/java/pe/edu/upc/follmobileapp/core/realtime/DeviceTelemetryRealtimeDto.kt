package pe.edu.upc.follmobileapp.core.realtime

/**
 * Espejo del payload `DeviceTelemetryRealtimeMessage` que emite el backend por
 * SignalR en el evento "device.telemetry" CADA heartbeat. Permite actualizar la
 * batería/estado del dispositivo en tiempo real sin recargar ni hacer polling.
 *
 * Los nombres coinciden con el JSON (camelCase) para que Gson los deserialice.
 */
data class DeviceTelemetryRealtimeDto(
    val deviceId: Long = 0L,
    val patientId: Long = 0L,
    val batteryLevel: Int = 0,
    val isCharging: Boolean = false,
    val isOnline: Boolean = false,
    val lastHeartbeatAt: String? = null
)
