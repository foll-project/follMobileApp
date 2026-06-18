package pe.edu.upc.follmobileapp.features.care.data.di

import android.content.Context
import pe.edu.upc.follmobileapp.core.di.CoreModule
import pe.edu.upc.follmobileapp.core.di.NetworkModule
import pe.edu.upc.follmobileapp.features.care.data.local.PatientLocalDataSource
import pe.edu.upc.follmobileapp.features.care.data.remote.services.PatientService
import pe.edu.upc.follmobileapp.features.care.data.repository.PatientRepositoryImpl
import pe.edu.upc.follmobileapp.features.care.domain.repository.PatientRepository

object CareDataModule {
    @Volatile
    private var patientLocalDataSource: PatientLocalDataSource? = null

    @Volatile
    private var patientService: PatientService? = null

    @Volatile
    private var patientRepository: PatientRepository? = null

    fun providePatientLocalDataSource(context: Context): PatientLocalDataSource {
        return patientLocalDataSource ?: synchronized(this) {
            patientLocalDataSource ?: run {
                val instance = PatientLocalDataSource(
                    patientDao = CoreModule.providePatientDao(context)
                )
                patientLocalDataSource = instance
                instance
            }
        }
    }

    fun providePatientService(context: Context): PatientService {
        return patientService ?: synchronized(this) {
            patientService ?: run {
                val instance = NetworkModule.provideRetrofit(context).create(PatientService::class.java)
                patientService = instance
                instance
            }
        }
    }

    fun providePatientRepository(context: Context): PatientRepository {
        return patientRepository ?: synchronized(this) {
            patientRepository ?: run {
                val instance = PatientRepositoryImpl(
                    localDataSource = providePatientLocalDataSource(context),
                    patientService = providePatientService(context)
                )
                patientRepository = instance
                instance
            }
        }
    }
}
