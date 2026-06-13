package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
    val expandedPatientIds: Set<Long> = emptySet()
)

class CareViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CareUiState())
    val uiState: StateFlow<CareUiState> = _uiState.asStateFlow()

    init {
        // Cargar data mock inicial simulando registros de base de datos
        loadPatients()
    }

    private fun loadPatients() {
        val initialPatients = listOf(
            PatientUiModel(
                id = 1L,
                name = "Carmen Rosa",
                role = CaregiverRole.OFFICIAL_GUARDIAN,
                deviceId = "HW-1001",
                isDeviceOn = true,
                batteryPercentage = 85,
                isCharging = false,
                isInEmergency = false
            ),
            PatientUiModel(
                id = 2L,
                name = "Don Roberto",
                role = CaregiverRole.SECONDARY_GUARDIAN,
                deviceId = "HW-1002",
                isDeviceOn = true,
                batteryPercentage = 42,
                isCharging = true,
                isInEmergency = true
            ),
            PatientUiModel(
                id = 3L,
                name = "Elena Soto",
                role = CaregiverRole.INVITED_GUARDIAN,
                deviceId = "HW-1003",
                isDeviceOn = false,
                batteryPercentage = 15,
                isCharging = false,
                isInEmergency = false
            )
        )
        _uiState.update { it.copy(patients = initialPatients) }
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
