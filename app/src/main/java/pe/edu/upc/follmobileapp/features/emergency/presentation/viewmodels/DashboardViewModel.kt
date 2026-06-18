package pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.edu.upc.follmobileapp.features.care.domain.repository.PatientRepository
import pe.edu.upc.follmobileapp.features.emergency.domain.repository.EmergencyRepository
import pe.edu.upc.follmobileapp.features.iam.domain.repository.AuthRepository

data class DashboardPatientState(
    val id: Long,
    val name: String,
    val isInEmergency: Boolean
)

data class DashboardUiState(
    val caregiverName: String = "",
    val patients: List<DashboardPatientState> = emptyList(),
    val totalFalsePositives: Int = 0,
    val totalRealFalls: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val totalEvents: Int
        get() = totalFalsePositives + totalRealFalls

    val activeEmergenciesCount: Int
        get() = patients.count { it.isInEmergency }
}

class DashboardViewModel(
    private val patientRepository: PatientRepository,
    private val emergencyRepository: EmergencyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeDashboardData()
        syncDashboardData()
    }

    private fun observeDashboardData() {
        viewModelScope.launch {
            authRepository.getLoggedInUser().collect { user ->
                _uiState.update { it.copy(caregiverName = user?.firstName ?: "Cuidador") }
            }
        }

        viewModelScope.launch {
            combine(
                patientRepository.getPatientsFlow(),
                emergencyRepository.getAlertsFlow(),
                emergencyRepository.getIncidentsFlow()
            ) { patientsList, activeAlertsList, incidentsList ->
                val activeEmergencyPatientIds = activeAlertsList.map { it.patientId }.toSet()
                
                val dashboardPatients = patientsList.map { patient ->
                    DashboardPatientState(
                        id = patient.id,
                        name = patient.fullName,
                        isInEmergency = activeEmergencyPatientIds.contains(patient.id)
                    )
                }

                val realFalls = incidentsList.count { it.isRealEmergency }
                val falsePositives = incidentsList.count { !it.isRealEmergency }

                Triple(dashboardPatients, realFalls, falsePositives)
            }.collect { (patients, realFalls, falsePositives) ->
                _uiState.update {
                    it.copy(
                        patients = patients,
                        totalRealFalls = realFalls,
                        totalFalsePositives = falsePositives
                    )
                }
            }
        }
    }

    fun syncDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val alertsResult = emergencyRepository.syncAlerts()
            if (alertsResult.isFailure) {
                _uiState.update { it.copy(errorMessage = "Error al conectar con el servidor") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
