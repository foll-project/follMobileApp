package pe.edu.upc.follmobileapp.features.communication.domain.models

data class CareRequest(
    val invitationId: Long,
    val patientId: Long,
    val patientFirstName: String,
    val patientLastName: String,
    val patientDni: String,
    val requesterUserId: Int,
    val requesterName: String,
    val requesterEmail: String?,
    val relationshipTypeId: Int,
    val relationshipName: String,
    val status: String,
    val expiresAt: String,
    val type: String
) {
    val patientName: String
        get() = "$patientFirstName $patientLastName".trim().ifEmpty { "Desconocido" }
}
