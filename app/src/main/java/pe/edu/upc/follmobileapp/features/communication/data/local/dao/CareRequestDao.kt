package pe.edu.upc.follmobileapp.features.communication.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pe.edu.upc.follmobileapp.features.communication.data.local.models.CareRequestEntity

@Dao
interface CareRequestDao {
    @Query("SELECT * FROM care_requests WHERE type = :type ORDER BY invitationId DESC")
    fun getCareRequestsByTypeFlow(type: String): Flow<List<CareRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareRequests(requests: List<CareRequestEntity>)

    @Query("DELETE FROM care_requests WHERE type = :type")
    suspend fun deleteCareRequestsByType(type: String)

    @Query("DELETE FROM care_requests WHERE invitationId = :id")
    suspend fun deleteCareRequestById(id: Long)

    @Query("UPDATE care_requests SET status = :status WHERE invitationId = :id")
    suspend fun updateStatus(id: Long, status: String)
}
