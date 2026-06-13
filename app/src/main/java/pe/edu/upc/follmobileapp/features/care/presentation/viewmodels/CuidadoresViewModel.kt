package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CaregiverUiModel(
    val id: Long,
    val name: String,
    val role: CaregiverRole
)

data class CuidadoresUiState(
    val patientId: Long = 0L,
    val patientName: String = "",
    val currentUserRole: CaregiverRole = CaregiverRole.SECONDARY_GUARDIAN,
    val caregivers: List<CaregiverUiModel> = emptyList(),
    val actionMessage: String? = null
)

class CuidadoresViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CuidadoresUiState())
    val uiState: StateFlow<CuidadoresUiState> = _uiState.asStateFlow()

    // Cargar datos de cuidadores según el ID del abuelito
    fun loadCaregivers(patientId: Long) {
        if (_uiState.value.patientId != 0L) return

        val (patientName, currentUserRole, caregiversList) = when (patientId) {
            1L -> Triple(
                "Carmen Rosa",
                CaregiverRole.OFFICIAL_GUARDIAN, // El usuario logueado es el Cuidador Principal
                listOf(
                    CaregiverUiModel(101L, "María Gonzales", CaregiverRole.OFFICIAL_GUARDIAN),
                    CaregiverUiModel(102L, "Jorge Silva", CaregiverRole.SECONDARY_GUARDIAN),
                    CaregiverUiModel(103L, "Lucía Méndez", CaregiverRole.INVITED_GUARDIAN)
                )
            )
            2L -> Triple(
                "Don Roberto",
                CaregiverRole.SECONDARY_GUARDIAN, // El usuario logueado es Cuidador Secundario
                listOf(
                    CaregiverUiModel(201L, "Pedro Ruiz", CaregiverRole.OFFICIAL_GUARDIAN),
                    CaregiverUiModel(202L, "Jorge Silva", CaregiverRole.SECONDARY_GUARDIAN)
                )
            )
            else -> Triple(
                "Elena Soto",
                CaregiverRole.INVITED_GUARDIAN, // El usuario logueado es Principal Invitado
                listOf(
                    CaregiverUiModel(301L, "Silvia Díaz", CaregiverRole.OFFICIAL_GUARDIAN),
                    CaregiverUiModel(302L, "Lucía Méndez", CaregiverRole.INVITED_GUARDIAN)
                )
            )
        }

        _uiState.update {
            it.copy(
                patientId = patientId,
                patientName = patientName,
                currentUserRole = currentUserRole,
                caregivers = caregiversList
            )
        }
    }

    fun removeCaregiver(caregiverId: Long) {
        _uiState.update { state ->
            val caregiver = state.caregivers.find { it.id == caregiverId }
            val updatedList = state.caregivers.filter { it.id != caregiverId }
            state.copy(
                caregivers = updatedList,
                actionMessage = caregiver?.let { "Cuidador ${it.name} removido con éxito" }
            )
        }
    }

    fun toggleCaregiverPromotion(caregiverId: Long) {
        _uiState.update { state ->
            var msg: String? = null
            val updatedList = state.caregivers.map { caregiver ->
                if (caregiver.id == caregiverId) {
                    val newRole = if (caregiver.role == CaregiverRole.INVITED_GUARDIAN) {
                        msg = "Asignación de Principal Invitado quitada a ${caregiver.name}"
                        CaregiverRole.SECONDARY_GUARDIAN
                    } else {
                        msg = "Cuidador ${caregiver.name} asignado como Principal Invitado"
                        CaregiverRole.INVITED_GUARDIAN
                    }
                    caregiver.copy(role = newRole)
                } else {
                    caregiver
                }
            }
            state.copy(
                caregivers = updatedList,
                actionMessage = msg
            )
        }
    }

    fun clearActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
