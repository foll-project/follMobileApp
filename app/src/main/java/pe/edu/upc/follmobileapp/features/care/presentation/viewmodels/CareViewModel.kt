package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.edu.upc.follmobileapp.features.care.domain.models.Patient
import pe.edu.upc.follmobileapp.features.care.domain.repository.PatientRepository
import pe.edu.upc.follmobileapp.features.iam.domain.repository.AuthRepository
import pe.edu.upc.follmobileapp.features.emergency.domain.repository.EmergencyRepository

enum class CaregiverRole(val label: String) {
    OFFICIAL_GUARDIAN("Cuidador Principal"),
    SECONDARY_GUARDIAN("Cuidador Secundario"),
    INVITED_GUARDIAN("Principal Invitado")
}

data class PatientUiModel(
    val id: Long,
    val name: String,
    val role: CaregiverRole,
    val deviceId: String,
    val isDeviceOn: Boolean,
    val batteryPercentage: Int,
    val isCharging: Boolean,
    val isInEmergency: Boolean = false
)

data class CareUiState(
    val patients: List<PatientUiModel> = emptyList(),
    val expandedPatientIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CareViewModel(
    private val patientRepository: PatientRepository,
    private val authRepository: AuthRepository,
    private val emergencyRepository: EmergencyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CareUiState())
    val uiState: StateFlow<CareUiState> = _uiState.asStateFlow()

    init {
        observePatients()
        syncPatientsFromRemote()
    }

    private fun observePatients() {
        viewModelScope.launch {
            combine(
                patientRepository.getPatientsFlow(),
                emergencyRepository.getAlertsFlow()
            ) { patientsList, activeAlerts ->
                val emergencyPatientIds = activeAlerts.map { it.patientId }.toSet()
                patientsList.map { patient ->
                    val role = when (patient.caregiverKind) {
                        "official" -> CaregiverRole.OFFICIAL_GUARDIAN
                        "invited" -> CaregiverRole.INVITED_GUARDIAN
                        else -> CaregiverRole.SECONDARY_GUARDIAN
                    }
                    PatientUiModel(
                        id = patient.id,
                        name = patient.fullName,
                        role = role,
                        deviceId = patient.device?.id ?: "Sin dispositivo",
                        isDeviceOn = patient.device?.status == "Online",
                        batteryPercentage = patient.device?.batteryPercentage ?: 0,
                        isCharging = patient.device?.isCharging ?: false,
                        isInEmergency = emergencyPatientIds.contains(patient.id)
                    )
                }
            }.collect { uiPatients ->
                _uiState.update { it.copy(patients = uiPatients) }
            }
        }
    }

    fun syncPatientsFromRemote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.getLoggedInUser().firstOrNull()?.let { user ->
                val result = patientRepository.syncPatients(user.userId)
                if (result.isFailure) {
                    _uiState.update { it.copy(errorMessage = "Error de sincronización con el servidor") }
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun togglePatientExpansion(patientId: Long) {
        _uiState.update { state ->
            val newExpanded = if (state.expandedPatientIds.contains(patientId)) {
                state.expandedPatientIds - patientId
            } else {
                state.expandedPatientIds + patientId
            }
            state.copy(expandedPatientIds = newExpanded)
        }
    }
}
