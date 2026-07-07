package pe.edu.upc.follmobileapp.core.di

import android.content.Context
import pe.edu.upc.follmobileapp.core.data.local.UserSessionCache
import pe.edu.upc.follmobileapp.core.data.local.database.FollDatabase
import pe.edu.upc.follmobileapp.features.care.data.local.dao.PatientDao
import pe.edu.upc.follmobileapp.features.iam.data.local.dao.UserDao
import pe.edu.upc.follmobileapp.features.emergency.data.local.dao.FallEventDao

object CoreModule {
    @Volatile
    private var userSessionCache: UserSessionCache? = null
    fun provideDatabase(context: Context): FollDatabase {
        return FollDatabase.getDatabase(context)
    }

    fun provideUserDao(context: Context): UserDao {
        return provideDatabase(context).userDao()
    }

    fun providePatientDao(context: Context): PatientDao {
        return provideDatabase(context).patientDao()
    }

    fun provideCareRequestDao(context: Context): pe.edu.upc.follmobileapp.features.communication.data.local.dao.CareRequestDao {
        return provideDatabase(context).careRequestDao()
    }

    fun provideFallEventDao(context: Context): FallEventDao {
        return provideDatabase(context).fallEventDao()
    }

    fun provideUserSessionCache(context: Context): UserSessionCache {
        return userSessionCache ?: synchronized(this) {
            userSessionCache ?: UserSessionCache(
                patientDao = providePatientDao(context),
                fallEventDao = provideFallEventDao(context),
                careRequestDao = provideCareRequestDao(context)
            ).also { userSessionCache = it }
        }
    }
}
