package pe.edu.upc.follmobileapp.features.communication.data.di

import android.content.Context
import pe.edu.upc.follmobileapp.core.di.CoreModule
import pe.edu.upc.follmobileapp.core.di.NetworkModule
import pe.edu.upc.follmobileapp.features.communication.data.local.CommunicationLocalDataSource
import pe.edu.upc.follmobileapp.features.communication.data.remote.services.InvitationService
import pe.edu.upc.follmobileapp.features.communication.data.repository.CommunicationRepositoryImpl
import pe.edu.upc.follmobileapp.features.communication.domain.repository.CommunicationRepository

object CommunicationModule {
    @Volatile
    private var localDataSource: CommunicationLocalDataSource? = null

    @Volatile
    private var invitationService: InvitationService? = null

    @Volatile
    private var repository: CommunicationRepository? = null

    fun provideLocalDataSource(context: Context): CommunicationLocalDataSource {
        return localDataSource ?: synchronized(this) {
            localDataSource ?: run {
                val instance = CommunicationLocalDataSource(
                    careRequestDao = CoreModule.provideCareRequestDao(context)
                )
                localDataSource = instance
                instance
            }
        }
    }

    fun provideInvitationService(context: Context): InvitationService {
        return invitationService ?: synchronized(this) {
            invitationService ?: run {
                val instance = NetworkModule.provideRetrofit(context).create(InvitationService::class.java)
                invitationService = instance
                instance
            }
        }
    }

    fun provideRepository(context: Context): CommunicationRepository {
        return repository ?: synchronized(this) {
            repository ?: run {
                val instance = CommunicationRepositoryImpl(
                    localDataSource = provideLocalDataSource(context),
                    invitationService = provideInvitationService(context)
                )
                repository = instance
                instance
            }
        }
    }
}
