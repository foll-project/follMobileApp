package pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DashboardPatientState(
    val id: Long,
    val name: String,
    val isInEmergency: Boolean
)

data class DashboardUiState(
    val caregiverName: String = "María",
    val patients: List<DashboardPatientState> = emptyList(),
    val totalFalsePositives: Int = 20,
    val totalRealFalls: Int = 4
) {
    val totalEvents: Int
        get() = totalFalsePositives + totalRealFalls

    val activeEmergenciesCount: Int
        get() = patients.count { it.isInEmergency }
}

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        // Mock data matching the rest of the application
        val mockPatients = listOf(
            DashboardPatientState(id = 1L, name = "Carmen Rosa", isInEmergency = false),
            DashboardPatientState(id = 2L, name = "Don Roberto", isInEmergency = true),
            DashboardPatientState(id = 3L, name = "Elena Soto", isInEmergency = false)
        )
        _uiState.update {
            it.copy(
                caregiverName = "María",
                patients = mockPatients,
                totalFalsePositives = 20,
                totalRealFalls = 4
            )
        }
    }
}
