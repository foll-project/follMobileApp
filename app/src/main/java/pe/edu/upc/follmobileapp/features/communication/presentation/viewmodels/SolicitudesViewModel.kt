package pe.edu.upc.follmobileapp.features.communication.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.edu.upc.follmobileapp.features.communication.domain.models.CareRequest
import pe.edu.upc.follmobileapp.features.communication.domain.repository.CommunicationRepository

data class SolicitudesUiState(
    val receivedPending: List<CareRequest> = emptyList(),
    val receivedHistory: List<CareRequest> = emptyList(),
    val sentInvitations: List<CareRequest> = emptyList(),
    val currentTab: String = "received", // "received" or "sent"
    val dniInput: String = "",
    val relationshipTypeId: Int = 4, // Default "Familiar" (ID: 4)
    val isLoading: Boolean = false,
    val actionMessage: String? = null,
    val isFormLoading: Boolean = false
)

class SolicitudesViewModel(
    private val repository: CommunicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SolicitudesUiState())
    val uiState: StateFlow<SolicitudesUiState> = _uiState.asStateFlow()

    init {
        observeInvitations()
        syncAll()
    }

    private fun observeInvitations() {
        viewModelScope.launch {
            repository.getReceivedRequestsFlow().collect { receivedList ->
                val pending = receivedList.filter { it.status.equals("Pending", ignoreCase = true) }
                val history = receivedList.filter { !it.status.equals("Pending", ignoreCase = true) }
                _uiState.update { state ->
                    state.copy(
                        receivedPending = pending,
                        receivedHistory = history
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getSentRequestsFlow().collect { sentList ->
                _uiState.update { state ->
                    state.copy(
                        sentInvitations = sentList
                    )
                }
            }
        }
    }

    fun syncAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val receivedResult = repository.syncReceivedRequests()
            val sentResult = repository.syncSentRequests()
            if (receivedResult.isFailure || sentResult.isFailure) {
                _uiState.update { it.copy(actionMessage = "Error de sincronización con el servidor") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onTabChanged(tab: String) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun onDniChanged(dni: String) {
        _uiState.update { it.copy(dniInput = dni) }
    }

    fun onRelationshipChanged(relationshipId: Int) {
        _uiState.update { it.copy(relationshipTypeId = relationshipId) }
    }

    fun sendInvitation() {
        val state = _uiState.value
        val cleanDni = state.dniInput.trim()
        if (cleanDni.length < 8) {
            _uiState.update { it.copy(actionMessage = "El DNI debe tener 8 dígitos") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isFormLoading = true) }
            val result = repository.sendInvitation(cleanDni, state.relationshipTypeId)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        dniInput = "",
                        relationshipTypeId = 4,
                        actionMessage = "Solicitud de acceso enviada correctamente",
                        currentTab = "sent",
                        isFormLoading = false
                    )
                }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Error al enviar la solicitud"
                _uiState.update {
                    it.copy(
                        actionMessage = errorMsg,
                        isFormLoading = false
                    )
                }
            }
        }
    }

    fun acceptInvitation(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.acceptInvitation(id)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        actionMessage = "Invitación aprobada correctamente",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        actionMessage = "Error al aprobar la invitación",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun rejectInvitation(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.rejectInvitation(id)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        actionMessage = "Invitación rechazada correctamente",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        actionMessage = "Error al rechazar la invitación",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
