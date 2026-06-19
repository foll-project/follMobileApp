package pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.edu.upc.follmobileapp.features.care.domain.repository.PatientRepository
import pe.edu.upc.follmobileapp.features.emergency.domain.models.EmergencyAlert
import pe.edu.upc.follmobileapp.features.emergency.domain.repository.EmergencyRepository

data class AlertAnnotation(
    val dateString: String,
    val authorName: String,
    val content: String
)

data class AlertUiState(
    val alerts: List<EmergencyAlert> = emptyList(),
    val patientAnnotations: Map<Long, List<AlertAnnotation>> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val actionMessage: String? = null
)

class AlertViewModel(
    private val emergencyRepository: EmergencyRepository,
    private val patientRepository: PatientRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlertUiState())
    val uiState: StateFlow<AlertUiState> = _uiState.asStateFlow()

    init {
        observeData()
        syncData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                emergencyRepository.getAlertsFlow(),
                patientRepository.getPatientsFlow()
            ) { alertsList, patientsList ->
                val annotationsMap = patientsList.associate { patient ->
                    val alertAnnotations = patient.annotations.map { ann ->
                        AlertAnnotation(
                            dateString = ann.dateString,
                            authorName = ann.authorName,
                            content = ann.content
                        )
                    }
                    patient.id to alertAnnotations
                }
                Pair(alertsList, annotationsMap)
            }.collect { (alerts, annotations) ->
                _uiState.update {
                    it.copy(alerts = alerts, patientAnnotations = annotations)
                }
            }
        }
    }

    fun syncData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = emergencyRepository.syncAlerts()
            if (result.isFailure) {
                _uiState.update { it.copy(errorMessage = "Error al conectar con el servidor") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun acknowledgeAlert(alertId: Long, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = emergencyRepository.acknowledgeAlert(alertId)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, actionMessage = "Emergencia atendida correctamente") }
                onSuccess()
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al atender la emergencia") }
            }
        }
    }

    /**
     * Atiende la emergencia: cierra el incidente en el backend y avisa en tiempo real
     * a los demás cuidadores. Tras esto la caída deja de estar activa y podrán llegar
     * nuevas caídas del dispositivo.
     */
    fun attendAlert(patientId: Long, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = emergencyRepository.attendFall(patientId)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(isLoading = false, actionMessage = "Atendiste esta emergencia. Se avisó a los demás cuidadores.")
                }
                onSuccess()
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudo atender la emergencia. Intenta de nuevo.") }
            }
        }
    }

    fun clearActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
