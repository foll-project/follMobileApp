package pe.edu.upc.follmobileapp.features.communication.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "care_requests")
data class CareRequestEntity(
    @PrimaryKey val invitationId: Long,
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
    val type: String // "sent" or "received"
)
