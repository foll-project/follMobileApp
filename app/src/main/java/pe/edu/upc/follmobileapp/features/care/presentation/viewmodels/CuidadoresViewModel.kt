package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.edu.upc.follmobileapp.features.care.domain.models.EmergencyContact
import pe.edu.upc.follmobileapp.features.care.domain.repository.PatientRepository

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
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val actionMessage: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CuidadoresViewModel(
    private val patientRepository: PatientRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CuidadoresUiState())
    val uiState: StateFlow<CuidadoresUiState> = _uiState.asStateFlow()

    private var patientObserveJob: kotlinx.coroutines.Job? = null

    fun loadCaregivers(patientId: Long) {
        if (_uiState.value.patientId == patientId) return

        patientObserveJob?.cancel()
        patientObserveJob = viewModelScope.launch {
            patientRepository.getPatientByIdFlow(patientId).collect { patient ->
                if (patient != null) {
                    val list = patient.caregivers.map { cg ->
                        val role = when (cg.role) {
                            "Principal" -> CaregiverRole.OFFICIAL_GUARDIAN
                            "Invitado" -> CaregiverRole.INVITED_GUARDIAN
                            else -> CaregiverRole.SECONDARY_GUARDIAN
                        }
                        CaregiverUiModel(
                            id = cg.id.toLongOrNull() ?: cg.hashCode().toLong(),
                            name = cg.name,
                            role = role
                        )
                    }
                    val currentUserRole = when (patient.caregiverKind) {
                        "official" -> CaregiverRole.OFFICIAL_GUARDIAN
                        "invited" -> CaregiverRole.INVITED_GUARDIAN
                        else -> CaregiverRole.SECONDARY_GUARDIAN
                    }
                    _uiState.update { state ->
                        state.copy(
                            patientId = patientId,
                            patientName = patient.fullName,
                            currentUserRole = currentUserRole,
                            caregivers = list,
                            emergencyContacts = patient.emergencyContacts
                        )
                    }
                }
            }
        }

        // Sync from server
        viewModelScope.launch {
            patientRepository.syncPatientDetails(patientId)
        }
    }

    fun removeCaregiver(caregiverId: Long) {
        // Visual deletion since backend doesn't expose delete caregiver yet
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
        val state = _uiState.value
        val caregiver = state.caregivers.find { it.id == caregiverId } ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = if (caregiver.role == CaregiverRole.INVITED_GUARDIAN) {
                patientRepository.restoreGuardian(state.patientId)
            } else {
                patientRepository.changeGuardian(state.patientId, caregiverId.toInt())
            }
            if (result.isSuccess) {
                val msg = if (caregiver.role == CaregiverRole.INVITED_GUARDIAN) {
                    "Asignación de Principal Invitado quitada a ${caregiver.name}"
                } else {
                    "Cuidador ${caregiver.name} asignado como Principal Invitado"
                }
                _uiState.update { it.copy(actionMessage = msg, isLoading = false) }
            } else {
                _uiState.update { it.copy(actionMessage = "Error al modificar mando principal", isLoading = false) }
            }
        }
    }

    fun addEmergencyContact(name: String, phoneNumber: String, relationship: String) {
        if (name.isBlank() || phoneNumber.isBlank() || relationship.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = patientRepository.createEmergencyContact(
                _uiState.value.patientId,
                name.trim(),
                phoneNumber.trim(),
                relationship.trim()
            )
            if (result.isSuccess) {
                _uiState.update { it.copy(actionMessage = "Contacto de emergencia agregado", isLoading = false) }
            } else {
                _uiState.update { it.copy(actionMessage = "Error al agregar contacto de emergencia", isLoading = false) }
            }
        }
    }

    fun deleteEmergencyContact(contactId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = patientRepository.deleteEmergencyContact(_uiState.value.patientId, contactId)
            if (result.isSuccess) {
                _uiState.update { it.copy(actionMessage = "Contacto de emergencia eliminado", isLoading = false) }
            } else {
                _uiState.update { it.copy(actionMessage = "Error al eliminar contacto de emergencia", isLoading = false) }
            }
        }
    }

    fun clearActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
