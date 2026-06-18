package pe.edu.upc.follmobileapp.core.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pe.edu.upc.follmobileapp.features.care.data.local.dao.PatientDao
import pe.edu.upc.follmobileapp.features.care.data.local.models.PatientEntity
import pe.edu.upc.follmobileapp.features.iam.data.local.dao.UserDao
import pe.edu.upc.follmobileapp.features.iam.data.local.models.UserEntity
import pe.edu.upc.follmobileapp.features.communication.data.local.dao.CareRequestDao
import pe.edu.upc.follmobileapp.features.communication.data.local.models.CareRequestEntity
import pe.edu.upc.follmobileapp.features.emergency.data.local.dao.FallEventDao
import pe.edu.upc.follmobileapp.features.emergency.data.local.models.FallEventEntity

@Database(entities = [UserEntity::class, PatientEntity::class, CareRequestEntity::class, FallEventEntity::class], version = 5, exportSchema = false)
@TypeConverters(FollConverters::class)
abstract class FollDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun patientDao(): PatientDao
    abstract fun careRequestDao(): CareRequestDao
    abstract fun fallEventDao(): FallEventDao

    companion object {
        @Volatile
        private var INSTANCE: FollDatabase? = null

        fun getDatabase(context: Context): FollDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FollDatabase::class.java,
                    "foll_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
