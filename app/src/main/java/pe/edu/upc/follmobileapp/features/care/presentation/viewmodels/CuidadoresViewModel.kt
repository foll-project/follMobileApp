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
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val actionMessage: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CuidadoresViewModel(
    private val patientId: Long,
    private val repository: PatientRepository
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    private val _actionMessage = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CuidadoresUiState> = combine(
        repository.getPatientByIdFlow(patientId),
        _isLoading,
        _actionMessage
    ) { patient, isLoading, actionMessage ->
        if (patient != null) {
            val currentUserRole = when (patient.caregiverKind) {
                "official" -> CaregiverRole.OFFICIAL_GUARDIAN
                "invited_primary" -> CaregiverRole.INVITED_GUARDIAN
                else -> CaregiverRole.SECONDARY_GUARDIAN
            }
            CuidadoresUiState(
                patientId = patient.id,
                patientName = patient.fullName,
                currentUserRole = currentUserRole,
                emergencyContacts = patient.emergencyContacts,
                isLoading = isLoading,
                actionMessage = actionMessage
            )
        } else {
            CuidadoresUiState(isLoading = isLoading, actionMessage = actionMessage)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CuidadoresUiState()
    )

    val caregivers = repository.getCaregiversByPatientId(patientId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadCaregivers(patientId: Long) {
        // Sync from server
        viewModelScope.launch {
            repository.syncPatientDetails(patientId)
        }
    }

    fun removeCaregiver(caregiverId: Long, caregiverName: String) {
        if (patientId == 0L) return
        
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.removeCaregiver(patientId, caregiverId)
            
            if (result.isSuccess) {
                _actionMessage.value = "Cuidador $caregiverName removido con éxito"
            } else {
                _actionMessage.value = "Error al remover cuidador: ${result.exceptionOrNull()?.message}"
            }
            _isLoading.value = false
        }
    }

    fun toggleCaregiverPromotion(caregiverId: Long, isInvited: Boolean, caregiverName: String) {
        if (patientId == 0L) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = if (isInvited) {
                repository.restoreGuardian(patientId)
            } else {
                repository.changeGuardian(patientId, caregiverId.toInt())
            }
            if (result.isSuccess) {
                _actionMessage.value = if (isInvited) {
                    "Asignación de Principal Invitado quitada a $caregiverName"
                } else {
                    "Cuidador $caregiverName asignado como Principal Invitado"
                }
            } else {
                _actionMessage.value = "Error al modificar mando principal"
            }
            _isLoading.value = false
        }
    }

    fun addEmergencyContact(name: String, phoneNumber: String, relationship: String) {
        if (name.isBlank() || phoneNumber.isBlank() || relationship.isBlank()) return
        if (patientId == 0L) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.createEmergencyContact(
                patientId,
                name.trim(),
                phoneNumber.trim(),
                relationship.trim()
            )
            if (result.isSuccess) {
                _actionMessage.value = "Contacto de emergencia agregado"
            } else {
                _actionMessage.value = "Error al agregar contacto de emergencia"
            }
            _isLoading.value = false
        }
    }

    fun deleteEmergencyContact(contactId: Long) {
        if (patientId == 0L) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteEmergencyContact(patientId, contactId)
            if (result.isSuccess) {
                _actionMessage.value = "Contacto de emergencia eliminado"
            } else {
                _actionMessage.value = "Error al eliminar contacto de emergencia"
            }
            _isLoading.value = false
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}

// Extensión para cumplir exactamente con la sintaxis requerida leyendo directo del flow
private fun PatientRepository.getCaregiversByPatientId(patientId: Long): Flow<List<CaregiverUiModel>> {
    return this.getPatientByIdFlow(patientId).map { patient ->
        patient?.caregivers?.map { cg ->
            val role = when (cg.role) {
                "Principal Oficial" -> CaregiverRole.OFFICIAL_GUARDIAN
                "Principal Invitado" -> CaregiverRole.INVITED_GUARDIAN
                else -> CaregiverRole.SECONDARY_GUARDIAN
            }
            CaregiverUiModel(
                id = cg.id.toLongOrNull() ?: cg.hashCode().toLong(),
                name = cg.name,
                role = role
            )
        } ?: emptyList()
    }
}
