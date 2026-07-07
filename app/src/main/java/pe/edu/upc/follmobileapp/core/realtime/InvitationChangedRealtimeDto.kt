package pe.edu.upc.follmobileapp.core.realtime

/**
 * Espejo del payload `InvitationChangedRealtimeMessage` que emite el backend por
 * SignalR en el evento "invitation.changed" cuando se crea, acepta o rechaza
 * una invitación de cuidado.
 */
data class InvitationChangedRealtimeDto(
    val kind: String? = null,
    val invitationId: Long = 0L,
    val patientId: Long = 0L,
    val patientName: String? = null,
    val requesterUserId: Long = 0L,
    val requesterName: String? = null,
    val relationshipTypeId: Int = 0,
    val relationshipName: String? = null,
    val status: String? = null,
    val title: String? = null,
    val message: String? = null,
    val occurredAt: String? = null
)
