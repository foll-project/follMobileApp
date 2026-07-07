package pe.edu.upc.follmobileapp.features.care.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pe.edu.upc.follmobileapp.features.care.data.local.models.PatientEntity

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients")
    fun getPatientsFlow(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE id = :id")
    fun getPatientByIdFlow(id: Long): Flow<PatientEntity?>

    @Query("SELECT * FROM patients WHERE id = :id")
    suspend fun getPatientById(id: Long): PatientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatients(patients: List<PatientEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity)

    @Query("DELETE FROM patients WHERE id = :id")
    suspend fun deletePatient(id: Long)

    @Query("DELETE FROM patients")
    suspend fun clearPatients()

    @Query("DELETE FROM patients WHERE id NOT IN (:ids)")
    suspend fun deletePatientsNotIn(ids: List<Long>)

    @Transaction
    suspend fun syncPatientsData(patients: List<PatientEntity>) {
        val newIds = patients.map { it.id }
        if (newIds.isEmpty()) {
            clearPatients()
        } else {
            deletePatientsNotIn(newIds)
            insertPatients(patients)
        }
    }
}
