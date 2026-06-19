package pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.edu.upc.follmobileapp.features.care.domain.repository.PatientRepository
import pe.edu.upc.follmobileapp.features.emergency.domain.models.FallIncident
import pe.edu.upc.follmobileapp.features.emergency.domain.repository.EmergencyRepository

data class FallAnnotation(
    val dateString: String,
    val authorName: String,
    val content: String
)

data class HistoryUiState(
    val incidents: List<FallIncident> = emptyList(),
    val expandedIncidentIds: Set<Long> = emptySet(),
    val editingIncidentId: Long? = null,
    val editingObservationsText: String = "",
    val patientAnnotations: Map<Long, List<FallAnnotation>> = emptyMap(),
    val actionMessage: String? = null,
    val selectedPatientId: Long? = null,
    val filterPatients: List<Pair<Long?, String>> = listOf(null to "Todos"),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val filteredIncidents: List<FallIncident>
        get() = if (selectedPatientId == null) {
            incidents
        } else {
            incidents.filter { it.patientId == selectedPatientId }
        }
}

class HistoryViewModel(
    private val emergencyRepository: EmergencyRepository,
    private val patientRepository: PatientRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        observeHistoryData()
        syncHistoryData()
    }

    private fun observeHistoryData() {
        viewModelScope.launch {
            combine(
                emergencyRepository.getIncidentsFlow(),
                patientRepository.getPatientsFlow()
            ) { incidentsList, patientsList ->
                val filters = listOf(null to "Todos") + patientsList.map { it.id to it.fullName }
                
                val annotationsMap = patientsList.associate { patient ->
                    val list = patient.annotations.map { ann ->
                        FallAnnotation(
                            dateString = ann.dateString,
                            authorName = ann.authorName,
                            content = ann.content
                        )
                    }
                    patient.id to list
                }

                Triple(incidentsList, filters, annotationsMap)
            }.collect { (incidents, filters, annotations) ->
                _uiState.update {
                    it.copy(
                        incidents = incidents,
                        filterPatients = filters,
                        patientAnnotations = annotations
                    )
                }
            }
        }
    }

    fun syncHistoryData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = emergencyRepository.syncAlerts()
            if (result.isFailure) {
                _uiState.update { it.copy(errorMessage = "Error al conectar con el servidor") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun selectPatientFilter(patientId: Long?) {
        _uiState.update { it.copy(selectedPatientId = patientId) }
    }

    fun toggleIncidentExpansion(incidentId: Long) {
        _uiState.update { state ->
            val expanded = state.expandedIncidentIds
            val newExpanded = if (expanded.contains(incidentId)) {
                expanded - incidentId
            } else {
                expanded + incidentId
            }
            state.copy(expandedIncidentIds = newExpanded)
        }
    }

    fun startEditing(incidentId: Long, currentObservations: String) {
        _uiState.update {
            it.copy(
                editingIncidentId = incidentId,
                editingObservationsText = currentObservations
            )
        }
    }

    fun onObservationsChange(text: String) {
        _uiState.update {
            it.copy(editingObservationsText = text)
        }
    }

    fun saveObservations() {
        val editingId = _uiState.value.editingIncidentId ?: return
        val newText = _uiState.value.editingObservationsText

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = emergencyRepository.saveObservations(editingId, newText)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        editingIncidentId = null,
                        editingObservationsText = "",
                        actionMessage = "Observación guardada correctamente",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al guardar observaciones",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun cancelEditing() {
        _uiState.update {
            it.copy(
                editingIncidentId = null,
                editingObservationsText = ""
            )
        }
    }

    fun clearActionMessage() {
        _uiState.update {
            it.copy(actionMessage = null)
        }
    }
}
