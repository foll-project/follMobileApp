package pe.edu.upc.follmobileapp.features.iam.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upc.follmobileapp.features.iam.domain.models.LoginCredentials
import pe.edu.upc.follmobileapp.features.iam.domain.models.RegisterData
import pe.edu.upc.follmobileapp.features.iam.domain.models.User

interface AuthRepository {
    suspend fun login(credentials: LoginCredentials): Result<User>
    suspend fun register(data: RegisterData): Result<String>
    suspend fun logout(): Result<Unit>
    fun getLoggedInUser(): Flow<User?>
    suspend fun updateLocalUser(firstName: String, lastName: String, email: String, phoneNumber: String): Result<Unit>
}
