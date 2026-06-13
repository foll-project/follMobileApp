package pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pe.edu.upc.follmobileapp.features.emergency.domain.models.FallIncident

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
    val filterPatients: List<Pair<Long?, String>> = listOf(
        null to "Todos",
        1L to "Carmen Rosa",
        2L to "Don Roberto",
        3L to "Elena Soto"
    )
) {
    val filteredIncidents: List<FallIncident>
        get() = if (selectedPatientId == null) {
            incidents
        } else {
            incidents.filter { it.patientId == selectedPatientId }
        }
}

class HistoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val initialIncidents = listOf(
            FallIncident(
                id = 1L,
                patientId = 3L,
                patientName = "Elena Soto",
                isRealEmergency = true,
                dateString = "Hoy",
                timeString = "14:32",
                fallType = "FRONTAL",
                responseTime = "2 min 14 seg",
                observations = "Caída detectada en la cocina. El usuario no respondió al estímulo inicial. Los servicios de emergencia fueron contactados automáticamente.",
                latitude = -12.0894,
                longitude = -77.0123
            ),
            FallIncident(
                id = 2L,
                patientId = 3L,
                patientName = "Elena Soto",
                isRealEmergency = false,
                dateString = "12 Oct",
                timeString = "09:15",
                fallType = "UNKNOWN",
                responseTime = "",
                observations = "El sensor se activó al sentarse bruscamente. Confirmado por el usuario como falsa alarma.",
                latitude = 0.0,
                longitude = 0.0
            ),
            FallIncident(
                id = 3L,
                patientId = 1L,
                patientName = "Carmen Rosa",
                isRealEmergency = false,
                dateString = "10 Oct",
                timeString = "18:40",
                fallType = "UNKNOWN",
                responseTime = "",
                observations = "Dispositivo sufrió una caída accidental desde la mesa de noche.",
                latitude = 0.0,
                longitude = 0.0
            ),
            FallIncident(
                id = 4L,
                patientId = 2L,
                patientName = "Don Roberto",
                isRealEmergency = true,
                dateString = "08 Oct",
                timeString = "11:20",
                fallType = "LATERAL",
                responseTime = "1 min 45 seg",
                observations = "Caída cerca a la cama al intentar levantarse. Atendido por el cuidador de guardia.",
                latitude = -12.1224,
                longitude = -77.0289
            )
        )

        // Simula la bitácora de cuidado (anotaciones) de cada paciente
        val annotations = mapOf(
            1L to listOf(
                FallAnnotation("11 Oct 2026, 09:00", "María Gonzales", "La abuelita se quejó de un dolor de cabeza fuerte en la mañana."),
                FallAnnotation("09 Oct 2026, 18:30", "Juan Silva", "No quiso cenar, dijo sentirse mareada.")
            ),
            2L to listOf(
                FallAnnotation("12 Oct 2026, 08:15", "Pedro Ruiz", "Se administró la dosis diaria de medicamentos a tiempo. Se encuentra tranquilo."),
                FallAnnotation("10 Oct 2026, 15:40", "Jorge Silva", "Salió a caminar al parque por 20 minutos. Se le vio de muy buen ánimo.")
            ),
            3L to listOf(
                FallAnnotation("11 Oct 2026, 12:00", "Silvia Díaz", "Control de glucosa en ayunas: 110 mg/dL. Almorzó de manera regular."),
                FallAnnotation("12 Oct 2026, 10:30", "Silvia Díaz", "Presentó un leve temblor en las manos, se le dio su té tranquilizante."),
                FallAnnotation("12 Oct 2026, 15:00", "Tú", "Realizó sus ejercicios de fisioterapia de piernas sin complicaciones.")
            )
        )

        _uiState.update {
            it.copy(incidents = initialIncidents, patientAnnotations = annotations)
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

        _uiState.update { state ->
            val updatedIncidents = state.incidents.map { incident ->
                if (incident.id == editingId) {
                    incident.copy(observations = newText)
                } else {
                    incident
                }
            }
            state.copy(
                incidents = updatedIncidents,
                editingIncidentId = null,
                editingObservationsText = "",
                actionMessage = "Observación guardada correctamente"
            )
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
