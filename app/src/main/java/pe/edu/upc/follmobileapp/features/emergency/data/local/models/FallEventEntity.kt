package pe.edu.upc.follmobileapp.features.emergency.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fall_events")
data class FallEventEntity(
    @PrimaryKey val notificationLogId: Long,
    val userId: Int,
    val notificationType: String,
    val notificationChannel: String,
    val notificationStatus: String,
    val title: String,
    val body: String,
    val dataJson: String?,
    val providerMessageId: String?,
    val errorMessage: String?,
    val patientId: Long?,
    val deviceId: Int?,
    val sentAt: String?,
    val readAt: String?,
    val acknowledgedAt: String?,
    val createdAt: String,
    val updatedAt: String?
)
