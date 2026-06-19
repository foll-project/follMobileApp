package pe.edu.upc.follmobileapp.features.emergency.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upc.follmobileapp.features.emergency.domain.models.EmergencyAlert
import pe.edu.upc.follmobileapp.features.emergency.domain.models.FallIncident

interface EmergencyRepository {
    fun getAlertsFlow(): Flow<List<EmergencyAlert>>
    fun getIncidentsFlow(): Flow<List<FallIncident>>
    suspend fun syncAlerts(): Result<Unit>
    suspend fun acknowledgeAlert(notificationId: Long): Result<Unit>

    /**
     * Atiende la caída activa de un paciente: cierra el incidente en el backend
     * (Resolved) y marca localmente las alertas como atendidas. El backend
     * notifica en tiempo real a los demás cuidadores quién la atendió.
     */
    suspend fun attendFall(patientId: Long): Result<Unit>
    suspend fun markPatientFallsAttendedLocally(patientId: Long): Result<Unit>
    suspend fun registerPushToken(token: String): Result<Unit>
    suspend fun saveObservations(incidentId: Long, observations: String): Result<Unit>
}
