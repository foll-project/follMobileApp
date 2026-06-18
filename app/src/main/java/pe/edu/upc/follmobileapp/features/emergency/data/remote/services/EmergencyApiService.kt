package pe.edu.upc.follmobileapp.features.emergency.data.remote.services

import pe.edu.upc.follmobileapp.features.emergency.data.remote.models.NotificationDto
import pe.edu.upc.follmobileapp.features.emergency.data.remote.models.PushTokenRequest
import retrofit2.http.*

interface EmergencyApiService {
    @GET("api/notifications")
    suspend fun getNotifications(): List<NotificationDto>

    @PUT("api/notifications/{id}/acknowledge")
    suspend fun acknowledgeNotification(
        @Path("id") id: Long
    )

    @POST("api/notifications/push-tokens")
    suspend fun registerPushToken(
        @Body request: PushTokenRequest
    )
}
