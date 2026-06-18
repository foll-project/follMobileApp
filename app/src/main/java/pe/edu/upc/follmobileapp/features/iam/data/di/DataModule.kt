package pe.edu.upc.follmobileapp.features.iam.data.di

import android.content.Context
import pe.edu.upc.follmobileapp.core.di.CoreModule
import pe.edu.upc.follmobileapp.core.di.NetworkModule
import pe.edu.upc.follmobileapp.features.iam.data.local.AuthLocalDataSource
import pe.edu.upc.follmobileapp.features.iam.data.remote.services.AuthService
import pe.edu.upc.follmobileapp.features.iam.data.repository.AuthRepositoryImpl
import pe.edu.upc.follmobileapp.features.iam.domain.repository.AuthRepository

object DataModule {
    @Volatile
    private var authLocalDataSource: AuthLocalDataSource? = null

    @Volatile
    private var authService: AuthService? = null

    @Volatile
    private var authRepository: AuthRepository? = null

    fun provideAuthLocalDataSource(context: Context): AuthLocalDataSource {
        return authLocalDataSource ?: synchronized(this) {
            authLocalDataSource ?: run {
                val instance = AuthLocalDataSource(
                    userDao = CoreModule.provideUserDao(context),
                    context = context
                )
                authLocalDataSource = instance
                instance
            }
        }
    }

    fun provideAuthService(context: Context): AuthService {
        return authService ?: synchronized(this) {
            authService ?: run {
                val instance = NetworkModule.provideRetrofit(context).create(AuthService::class.java)
                authService = instance
                instance
            }
        }
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        return authRepository ?: synchronized(this) {
            authRepository ?: run {
                val instance = AuthRepositoryImpl(
                    localDataSource = provideAuthLocalDataSource(context),
                    authService = provideAuthService(context)
                )
                authRepository = instance
                instance
            }
        }
    }
}
