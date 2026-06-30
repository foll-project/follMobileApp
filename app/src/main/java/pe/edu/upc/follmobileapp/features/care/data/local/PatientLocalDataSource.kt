package pe.edu.upc.follmobileapp.features.care.data.local

import kotlinx.coroutines.flow.Flow
import pe.edu.upc.follmobileapp.features.care.data.local.dao.PatientDao
import pe.edu.upc.follmobileapp.features.care.data.local.models.PatientEntity

class PatientLocalDataSource(
    private val patientDao: PatientDao
) {
    fun getPatientsFlow(): Flow<List<PatientEntity>> = patientDao.getPatientsFlow()

    fun getPatientByIdFlow(id: Long): Flow<PatientEntity?> = patientDao.getPatientByIdFlow(id)

    suspend fun getPatientById(id: Long): PatientEntity? = patientDao.getPatientById(id)

    suspend fun savePatients(patients: List<PatientEntity>) {
        patientDao.insertPatients(patients)
    }

    suspend fun savePatient(patient: PatientEntity) {
        patientDao.insertPatient(patient)
    }

    suspend fun deletePatient(id: Long) {
        patientDao.deletePatient(id)
    }

    suspend fun clearPatients() {
        patientDao.clearPatients()
    }

    suspend fun syncPatientsData(patients: List<PatientEntity>) {
        patientDao.syncPatientsData(patients)
    }
}
