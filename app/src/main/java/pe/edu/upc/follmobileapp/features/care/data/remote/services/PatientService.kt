package pe.edu.upc.follmobileapp.features.care.data.remote.services

import pe.edu.upc.follmobileapp.features.care.data.remote.models.*
import retrofit2.http.*

interface PatientService {
    @POST("api/care/patients")
    suspend fun createPatient(
        @Body request: CreatePatientRequest
    )

    @DELETE("api/care/patients/{id}")
    suspend fun deletePatient(
        @Path("id") id: Long
    ): retrofit2.Response<Unit>

    @POST("api/patients/{patientId}/caregivers/qr")
    suspend fun linkCaregiverViaQr(
        @Path("patientId") patientId: Long,
        @Body request: LinkCaregiverQrRequest
    )


    @GET("api/care/patients/by-caregiver/{caregiverUserId}")
    suspend fun getPatientsByCaregiver(
        @Path("caregiverUserId") caregiverUserId: Int
    ): List<PatientResponseDto>

    @GET("api/care/patients/{id}")
    suspend fun getPatientById(
        @Path("id") id: Long
    ): PatientResponseDto

    @PUT("api/care/patients/{id}")
    suspend fun updatePatient(
        @Path("id") id: Long,
        @Body request: UpdatePatientRequest
    )

    @POST("api/care/patients/{id}/annotations")
    suspend fun createAnnotation(
        @Path("id") id: Long,
        @Body request: CreateAnnotationRequest
    )

    @GET("api/care/patients/{id}/annotations")
    suspend fun getAnnotations(
        @Path("id") id: Long
    ): List<AnnotationDto>

    @GET("api/care/patients/{id}/caregivers")
    suspend fun getCaregivers(
        @Path("id") id: Long
    ): List<CaregiverDto>

    @PUT("api/care/patients/{id}/guard-shift")
    suspend fun changeGuardian(
        @Path("id") id: Long,
        @Body request: ChangeGuardianRequest
    )

    @POST("api/care/patients/{id}/guard-shift/restore")
    suspend fun restoreGuardian(
        @Path("id") id: Long
    )

    @POST("api/care/patients/{id}/emergency-contacts")
    suspend fun createEmergencyContact(
        @Path("id") id: Long,
        @Body request: CreateEmergencyContactRequest
    )

    @DELETE("api/care/patients/{id}/emergency-contacts/{contactId}")
    suspend fun deleteEmergencyContact(
        @Path("id") id: Long,
        @Path("contactId") contactId: Long
    )

    @DELETE("api/patients/{id}/caregivers/{caregiverId}")
    suspend fun removeCaregiver(
        @Path("id") id: Long,
        @Path("caregiverId") caregiverId: Long
    )
}
