package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    val actionMessage: String? = null
)

class AnotacionesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AnotacionesUiState())
    val uiState: StateFlow<AnotacionesUiState> = _uiState.asStateFlow()

    // Cargar anotaciones según el ID del abuelito
    fun loadAnnotations(patientId: Long) {
        if (_uiState.value.patientId != 0L) return

        val (patientName, list) = when (patientId) {
            1L -> Pair(
                "Carmen Rosa",
                listOf(
                    AnnotationUiModel(
                        id = 101L,
                        dateString = "11 Oct 2026, 09:00",
                        authorName = "María Gonzales",
                        content = "La abuelita se quejó de un dolor de cabeza fuerte en la mañana."
                    ),
                    AnnotationUiModel(
                        id = 102L,
                        dateString = "09 Oct 2026, 18:30",
                        authorName = "Juan Silva",
                        content = "No quiso cenar, dijo sentirse mareada."
                    )
                )
            )
            2L -> Pair(
                "Roberto Silva", // Coincidente con la imagen
                listOf(
                    AnnotationUiModel(
                        id = 201L,
                        dateString = "12 Oct 2026, 08:15",
                        authorName = "Pedro Ruiz",
                        content = "Se administró la dosis diaria de medicamentos a tiempo. Se encuentra tranquilo."
                    ),
                    AnnotationUiModel(
                        id = 202L,
                        dateString = "10 Oct 2026, 15:40",
                        authorName = "Jorge Silva",
                        content = "Salió a caminar al parque por 20 minutos. Se le vio de muy buen ánimo."
                    )
                )
            )
            else -> Pair(
                "Elena Soto",
                listOf(
                    AnnotationUiModel(
                        id = 301L,
                        dateString = "11 Oct 2026, 12:00",
                        authorName = "Silvia Díaz",
                        content = "Control de glucosa en ayunas: 110 mg/dL. Almorzó de manera regular."
                    )
                )
            )
        }

        _uiState.update {
            it.copy(
                patientId = patientId,
                patientName = patientName,
                annotations = list
            )
        }
    }

    fun onNewAnnotationTextChange(text: String) {
        _uiState.update { it.copy(newAnnotationText = text) }
    }

    fun publishAnnotation() {
        val text = _uiState.value.newAnnotationText
        if (text.isBlank()) return

        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-PE"))
        val currentDateStr = dateFormat.format(Date())

        val newAnnotation = AnnotationUiModel(
            id = System.currentTimeMillis(),
            dateString = currentDateStr,
            authorName = "Tú",
            content = text.trim()
        )

        _uiState.update { state ->
            state.copy(
                annotations = listOf(newAnnotation) + state.annotations,
                newAnnotationText = "",
                actionMessage = "Anotación agregada correctamente"
            )
        }
    }

    fun clearActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
