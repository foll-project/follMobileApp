package pe.edu.upc.follmobileapp.core.realtime

/**
 * Espejo del payload `IncidentResolvedRealtimeMessage` que emite el backend por
 * SignalR en el evento "incident.resolved" cuando un cuidador atiende una caída.
 * Sirve para avisar EN TIEMPO REAL a los demás cuidadores quién la atendió.
 */
data class IncidentResolvedRealtimeDto(
    val incidentKey: String? = null,
    val deviceId: Long = 0L,
    val patientId: Long = 0L,
    val status: String? = null,
    val cancellationReason: String? = null,
    val closedByUserId: Long? = null,
    val closedByName: String? = null,
    val closedAt: String? = null,
    val observation: String? = null,
    val fallTypeName: String? = null
)
