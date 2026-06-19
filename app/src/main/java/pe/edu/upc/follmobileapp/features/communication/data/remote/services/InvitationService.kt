package pe.edu.upc.follmobileapp.features.communication.data.remote.services

import pe.edu.upc.follmobileapp.features.communication.data.remote.models.InvitationResponseDto
import pe.edu.upc.follmobileapp.features.communication.data.remote.models.SendInvitationRequest
import retrofit2.http.*

interface InvitationService {
    @GET("api/care/invitations/received")
    suspend fun getReceivedInvitations(): List<InvitationResponseDto>

    @GET("api/care/invitations/sent")
    suspend fun getSentInvitations(): List<InvitationResponseDto>

    @POST("api/care/patients/{dni}/invitations")
    suspend fun sendInvitation(
        @Path("dni") dni: String,
        @Body request: SendInvitationRequest
    ): InvitationResponseDto

    @POST("api/care/invitations/{id}/accept")
    suspend fun acceptInvitation(
        @Path("id") id: Long
    )

    @POST("api/care/invitations/{id}/reject")
    suspend fun rejectInvitation(
        @Path("id") id: Long
    )
}
