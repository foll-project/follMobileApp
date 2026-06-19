package pe.edu.upc.follmobileapp.core.di

import android.content.Context
import pe.edu.upc.follmobileapp.core.realtime.NotificationRealtimeService
import pe.edu.upc.follmobileapp.features.care.data.di.CareDataModule
import pe.edu.upc.follmobileapp.features.emergency.data.di.EmergencyModule
import pe.edu.upc.follmobileapp.features.iam.data.di.DataModule

object RealtimeModule {
    @Volatile
    private var service: NotificationRealtimeService? = null

    fun provideNotificationRealtimeService(context: Context): NotificationRealtimeService {
        return service ?: synchronized(this) {
            service ?: run {
                val instance = NotificationRealtimeService(
                    fallEventDao = CoreModule.provideFallEventDao(context),
                    patientRepository = CareDataModule.providePatientRepository(context),
                    authLocalDataSource = DataModule.provideAuthLocalDataSource(context)
                )
                service = instance
                instance
            }
        }
    }
}
