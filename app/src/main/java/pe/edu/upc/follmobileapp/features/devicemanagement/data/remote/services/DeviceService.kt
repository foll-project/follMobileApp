package pe.edu.upc.follmobileapp.features.devicemanagement.data.remote.services

import pe.edu.upc.follmobileapp.features.devicemanagement.data.remote.models.DeviceResponseDto
import pe.edu.upc.follmobileapp.features.devicemanagement.data.remote.models.LinkDeviceRequest
import retrofit2.http.*

interface DeviceService {
    @POST("api/devices/{deviceId}/link")
    suspend fun linkDevice(
        @Path("deviceId") deviceId: Int,
        @Body request: LinkDeviceRequest
    )

    @DELETE("api/devices/{deviceId}/link")
    suspend fun unlinkDevice(
        @Path("deviceId") deviceId: Int
    )

    @GET("api/devices/patient/{patientId}")
    suspend fun getDeviceByPatient(
        @Path("patientId") patientId: Long
    ): DeviceResponseDto
}
