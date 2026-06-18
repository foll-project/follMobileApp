package pe.edu.upc.follmobileapp.features.emergency.data.di

import android.content.Context
import pe.edu.upc.follmobileapp.core.di.CoreModule
import pe.edu.upc.follmobileapp.core.di.NetworkModule
import pe.edu.upc.follmobileapp.features.emergency.data.remote.services.EmergencyApiService
import pe.edu.upc.follmobileapp.features.emergency.data.repository.EmergencyRepositoryImpl
import pe.edu.upc.follmobileapp.features.emergency.domain.repository.EmergencyRepository

object EmergencyModule {
    @Volatile
    private var apiService: EmergencyApiService? = null

    @Volatile
    private var repository: EmergencyRepository? = null

    fun provideApiService(context: Context): EmergencyApiService {
        return apiService ?: synchronized(this) {
            apiService ?: run {
                val instance = NetworkModule.provideRetrofit(context).create(EmergencyApiService::class.java)
                apiService = instance
                instance
            }
        }
    }

    fun provideRepository(context: Context): EmergencyRepository {
        return repository ?: synchronized(this) {
            repository ?: run {
                val instance = EmergencyRepositoryImpl(
                    fallEventDao = CoreModule.provideFallEventDao(context),
                    patientDao = CoreModule.providePatientDao(context),
                    apiService = provideApiService(context)
                )
                repository = instance
                instance
            }
        }
    }
}
