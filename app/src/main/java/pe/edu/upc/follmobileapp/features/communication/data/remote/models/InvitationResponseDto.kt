package pe.edu.upc.follmobileapp.features.communication.data.remote.models

data class InvitationResponseDto(
    val invitationId: Long?,
    val patientId: Long?,
    val patientFirstName: String?,
    val patientLastName: String?,
    val patientName: String?,
    val patientDni: String?,
    val requesterUserId: Int?,
    val requesterName: String?,
    val requesterEmail: String?,
    val relationshipTypeId: Int?,
    val relationshipName: String?,
    val status: String?,
    val expiresAt: String?
)

data class SendInvitationRequest(
    val relationshipTypeId: Int
)
