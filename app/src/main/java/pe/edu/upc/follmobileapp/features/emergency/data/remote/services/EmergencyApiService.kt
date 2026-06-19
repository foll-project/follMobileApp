package pe.edu.upc.follmobileapp.features.emergency.data.remote.services

import pe.edu.upc.follmobileapp.features.emergency.data.remote.models.*
import retrofit2.http.*

interface EmergencyApiService {
    @GET("api/notifications")
    suspend fun getNotifications(): List<NotificationDto>

    @POST("api/notifications/{id}/acknowledge")
    suspend fun acknowledgeNotification(
        @Path("id") id: Long
    )

    @POST("api/notifications/push-tokens")
    suspend fun registerPushToken(
        @Body request: PushTokenRequest
    )

    // Incidente de emergencia (caída) activo de un paciente. Devuelve 404 si no hay ninguno abierto.
    @GET("api/emergency/incidents/active/patient/{patientId}")
    suspend fun getActiveIncident(
        @Path("patientId") patientId: Long
    ): ActiveIncidentDto

    // "Atender": cierra el incidente (Resolved). El backend avisa en tiempo real a los demás cuidadores.
    @POST("api/emergency/incidents/{incidentId}/resolve")
    suspend fun resolveIncident(
        @Path("incidentId") incidentId: Long,
        @Body request: ResolveIncidentRequest
    )
}
