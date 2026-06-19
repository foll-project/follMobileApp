package pe.edu.upc.follmobileapp.features.iam.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upc.follmobileapp.features.iam.data.local.AuthLocalDataSource
import pe.edu.upc.follmobileapp.features.iam.data.local.models.UserEntity
import pe.edu.upc.follmobileapp.features.iam.data.remote.models.LoginRequest
import pe.edu.upc.follmobileapp.features.iam.data.remote.models.RegisterRequest
import pe.edu.upc.follmobileapp.features.iam.data.remote.services.AuthService
import pe.edu.upc.follmobileapp.features.iam.domain.models.LoginCredentials
import pe.edu.upc.follmobileapp.features.iam.domain.models.RegisterData
import pe.edu.upc.follmobileapp.features.iam.domain.models.User
import pe.edu.upc.follmobileapp.features.iam.domain.repository.AuthRepository
import java.io.IOException

class AuthRepositoryImpl(
    private val localDataSource: AuthLocalDataSource,
    private val authService: AuthService
) : AuthRepository {

    override suspend fun login(credentials: LoginCredentials): Result<User> {
        return try {
            val request = LoginRequest(credentials.email, credentials.password)
            val response = authService.login(request)
            
            val userEntity = UserEntity(
                userId = response.userId,
                email = response.email,
                firstName = response.firstName,
                lastName = response.lastName,
                phoneNumber = response.phoneNumber,
                token = response.token
            )
            localDataSource.saveSession(userEntity)

            val domainUser = User(
                userId = response.userId,
                email = response.email,
                firstName = response.firstName,
                lastName = response.lastName,
                phoneNumber = response.phoneNumber,
                token = response.token
            )
            Result.success(domainUser)
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión al servidor", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(data: RegisterData): Result<String> {
        return try {
            val request = RegisterRequest(
                email = data.email,
                password = data.password,
                firstName = data.firstName,
                lastName = data.lastName,
                phoneNumber = data.phoneNumber
            )
            val response = authService.register(request)
            Result.success(response.message)
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión al servidor", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            try {
                authService.logout()
            } catch (e: Exception) {
                // Si falla la llamada de red o el token ya expiró, de todas formas
                // debemos limpiar la sesión local para asegurar la usabilidad.
            }
            localDataSource.clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getLoggedInUser(): Flow<User?> {
        return localDataSource.getLoggedInUserFlow().map { entity ->
            entity?.let {
                User(
                    userId = it.userId,
                    email = it.email,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    phoneNumber = it.phoneNumber,
                    token = it.token
                )
            }
        }
    }

    override suspend fun updateLocalUser(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String
    ): Result<Unit> {
        return try {
            val current = localDataSource.getLoggedInUser()
            if (current != null) {
                val updated = current.copy(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phoneNumber = phoneNumber
                )
                localDataSource.saveSession(updated)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
