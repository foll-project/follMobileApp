package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.edu.upc.follmobileapp.features.care.domain.repository.PatientRepository
import pe.edu.upc.follmobileapp.features.iam.domain.repository.AuthRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AnnotationUiModel(
    val id: Long,
    val dateString: String,
    val authorName: String,
    val content: String
)

data class AnotacionesUiState(
    val patientId: Long = 0L,
    val patientName: String = "",
    val annotations: List<AnnotationUiModel> = emptyList(),
    val newAnnotationText: String = "",
    val actionMessage: String? = null,
    val isLoading: Boolean = false
)

class AnotacionesViewModel(
    private val patientRepository: PatientRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AnotacionesUiState())
    val uiState: StateFlow<AnotacionesUiState> = _uiState.asStateFlow()

    private var patientObserveJob: kotlinx.coroutines.Job? = null

    fun loadAnnotations(patientId: Long) {
        if (_uiState.value.patientId == patientId) return

        patientObserveJob?.cancel()
        patientObserveJob = viewModelScope.launch {
            patientRepository.getPatientByIdFlow(patientId).collect { patient ->
                if (patient != null) {
                    val list = patient.annotations.map { ann ->
                        AnnotationUiModel(
                            id = ann.id.toLongOrNull() ?: ann.hashCode().toLong(),
                            dateString = ann.dateString,
                            authorName = ann.authorName,
                            content = ann.content
                        )
                    }
                    _uiState.update { state ->
                        state.copy(
                            patientId = patientId,
                            patientName = patient.fullName,
                            annotations = list
                        )
                    }
                }
            }
        }

        // Sync annotations list from network
        viewModelScope.launch {
            patientRepository.syncAnnotations(patientId)
        }
    }

    fun onNewAnnotationTextChange(text: String) {
        _uiState.update { it.copy(newAnnotationText = text) }
    }

    fun publishAnnotation() {
        val text = _uiState.value.newAnnotationText
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = patientRepository.addAnnotation(_uiState.value.patientId, text.trim())
            if (result.isSuccess) {
                _uiState.update { state ->
                    state.copy(
                        newAnnotationText = "",
                        actionMessage = "Anotación agregada correctamente",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, actionMessage = "Error al guardar la anotación") }
            }
        }
    }

    fun clearActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
