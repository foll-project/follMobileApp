package pe.edu.upc.follmobileapp.features.care.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upc.follmobileapp.features.care.domain.models.Caregiver
import pe.edu.upc.follmobileapp.features.care.domain.models.Patient

interface PatientRepository {
    fun getPatientsFlow(): Flow<List<Patient>>
    fun getPatientByIdFlow(id: Long): Flow<Patient?>
    suspend fun syncPatients(caregiverUserId: Int): Result<Unit>
    suspend fun syncPatientDetails(patientId: Long): Result<Unit>
    suspend fun createPatient(
        dni: String,
        name: String,
        bloodType: String,
        illnesses: List<String>,
        medications: List<String>
    ): Result<Unit>
    suspend fun updatePatient(
        id: Long,
        name: String,
        bloodType: String,
        illnesses: List<String>,
        medications: List<String>
    ): Result<Unit>
    suspend fun deletePatientLocally(id: Long): Result<Unit>
    suspend fun updateDeviceTelemetry(
        patientId: Long,
        batteryLevel: Int,
        isCharging: Boolean,
        isOnline: Boolean,
        lastHeartbeatAt: String?
    ): Result<Unit>
    suspend fun addAnnotation(patientId: Long, content: String): Result<Unit>
    suspend fun getCaregivers(patientId: Long): Result<List<Caregiver>>
    suspend fun changeGuardian(patientId: Long, newCurrentGuardianUserId: Int): Result<Unit>
    suspend fun restoreGuardian(patientId: Long): Result<Unit>
    suspend fun createEmergencyContact(
        patientId: Long,
        name: String,
        phoneNumber: String,
        relationship: String
    ): Result<Unit>
    suspend fun deleteEmergencyContact(patientId: Long, contactId: Long): Result<Unit>
    suspend fun syncAnnotations(patientId: Long): Result<Unit>
}
