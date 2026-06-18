package pe.edu.upc.follmobileapp.features.iam.data.remote.services

import pe.edu.upc.follmobileapp.features.iam.data.remote.models.AuthResponseDto
import pe.edu.upc.follmobileapp.features.iam.data.remote.models.LoginRequest
import pe.edu.upc.follmobileapp.features.iam.data.remote.models.RegisterRequest
import pe.edu.upc.follmobileapp.features.iam.data.remote.models.RegisterResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/iam/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponseDto

    @POST("api/iam/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponseDto

    @POST("api/iam/auth/logout")
    suspend fun logout(): retrofit2.Response<Unit>
}
