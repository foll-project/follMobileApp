package pe.edu.upc.follmobileapp.features.emergency.data.remote.models

data class NotificationDto(
    val notificationLogId: Long?,
    val userId: Int?,
    val notificationType: String?,
    val notificationChannel: String?,
    val notificationStatus: String?,
    val title: String?,
    val body: String?,
    val dataJson: String?,
    val providerMessageId: String?,
    val errorMessage: String?,
    val patientId: Long?,
    val deviceId: Int?,
    val sentAt: String?,
    val readAt: String?,
    val acknowledgedAt: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class PushTokenRequest(
    val token: String
)
