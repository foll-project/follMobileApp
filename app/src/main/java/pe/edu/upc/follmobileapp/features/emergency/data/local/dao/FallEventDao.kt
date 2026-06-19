package pe.edu.upc.follmobileapp.features.emergency.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pe.edu.upc.follmobileapp.features.emergency.data.local.models.FallEventEntity

@Dao
interface FallEventDao {
    @Query("SELECT * FROM fall_events ORDER BY createdAt DESC")
    fun getAlertsFlow(): Flow<List<FallEventEntity>>

    @Query("SELECT * FROM fall_events")
    suspend fun getAllOnce(): List<FallEventEntity>

    @Query("SELECT * FROM fall_events WHERE notificationLogId = :id LIMIT 1")
    suspend fun getById(id: Long): FallEventEntity?

    @Query(
        """
        SELECT * FROM fall_events
        WHERE notificationType = 'FallDetected'
          AND patientId = :patientId
          AND (acknowledgedAt IS NULL OR acknowledgedAt = '')
        """
    )
    suspend fun getUnacknowledgedFallsByPatient(patientId: Long): List<FallEventEntity>

    @Query(
        """
        SELECT DISTINCT patientId FROM fall_events
        WHERE notificationType = 'FallDetected'
          AND patientId IS NOT NULL
          AND (acknowledgedAt IS NULL OR acknowledgedAt = '')
        """
    )
    suspend fun getPatientIdsWithUnacknowledgedFalls(): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAlerts(alerts: List<FallEventEntity>)

    @Query("UPDATE fall_events SET acknowledgedAt = :acknowledgedAt WHERE notificationLogId = :id")
    suspend fun updateAcknowledge(id: Long, acknowledgedAt: String)

    // Marca como atendidas TODAS las caídas activas de un paciente (cuando alguien atiende el incidente).
    @Query("UPDATE fall_events SET acknowledgedAt = :acknowledgedAt WHERE patientId = :patientId AND notificationType = 'FallDetected' AND (acknowledgedAt IS NULL OR acknowledgedAt = '')")
    suspend fun acknowledgeFallsByPatient(patientId: Long, acknowledgedAt: String)

    @Query("UPDATE fall_events SET body = :observations WHERE notificationLogId = :id")
    suspend fun updateObservations(id: Long, observations: String)

    @Query("DELETE FROM fall_events")
    suspend fun clearAll()
}
