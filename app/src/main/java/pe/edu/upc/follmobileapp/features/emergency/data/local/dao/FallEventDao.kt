package pe.edu.upc.follmobileapp.features.emergency.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pe.edu.upc.follmobileapp.features.emergency.data.local.models.FallEventEntity

@Dao
interface FallEventDao {
    @Query("SELECT * FROM fall_events ORDER BY createdAt DESC")
    fun getAlertsFlow(): Flow<List<FallEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAlerts(alerts: List<FallEventEntity>)

    @Query("UPDATE fall_events SET acknowledgedAt = :acknowledgedAt WHERE notificationLogId = :id")
    suspend fun updateAcknowledge(id: Long, acknowledgedAt: String)

    @Query("UPDATE fall_events SET body = :observations WHERE notificationLogId = :id")
    suspend fun updateObservations(id: Long, observations: String)

    @Query("DELETE FROM fall_events")
    suspend fun clearAll()
}
