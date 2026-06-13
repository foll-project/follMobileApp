package pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EmergencyAlert(
    val id: Long,
    val patientId: Long,
    val patientName: String,
    val fallType: String,
    val elapsedMinutes: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val bloodType: String,
    val age: Int,
    val medicalConditions: String,
    val medications: String,
    val dni: String
)

data class AlertAnnotation(
    val dateString: String,
    val authorName: String,
    val content: String
)

data class AlertUiState(
    val alerts: List<EmergencyAlert> = emptyList(),
    val patientAnnotations: Map<Long, List<AlertAnnotation>> = emptyMap()
)

class AlertViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AlertUiState())
    val uiState: StateFlow<AlertUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val mockAlerts = listOf(
            EmergencyAlert(
                id = 1L,
                patientId = 2L,
                patientName = "Don Roberto",
                fallType = "Caída Lateral (Severidad Alta)",
                elapsedMinutes = 2,
                address = "Av. La Marina 2450, San Miguel, Lima",
                latitude = -12.0834,
                longitude = -77.0856,
                bloodType = "O+",
                age = 82,
                medicalConditions = "Hipertensión Arterial, Arritmia Leve",
                medications = "Losartán 50mg, Amiodarona 200mg",
                dni = "09214488"
            ),
            EmergencyAlert(
                id = 2L,
                patientId = 3L,
                patientName = "Elena Soto",
                fallType = "Caída Frontal (Severidad Media)",
                elapsedMinutes = 15,
                address = "Calle Los Álamos 123, Lince, Lima",
                latitude = -12.0894,
                longitude = -77.0123,
                bloodType = "AB-",
                age = 79,
                medicalConditions = "Osteoporosis, Diabetes Tipo 2",
                medications = "Metformina 850mg, Calcio + Vitamina D",
                dni = "08765412"
            )
        )

        val annotations = mapOf(
            2L to listOf(
                AlertAnnotation("12 Oct 2026, 08:15", "Pedro Ruiz", "Se administró la dosis diaria de medicamentos a tiempo. Se encuentra tranquilo."),
                AlertAnnotation("10 Oct 2026, 15:40", "Jorge Silva", "Salió a caminar al parque por 20 minutos. Se le vio de muy buen ánimo.")
            ),
            3L to listOf(
                AlertAnnotation("11 Oct 2026, 12:00", "Silvia Díaz", "Control de glucosa en ayunas: 110 mg/dL. Almorzó de manera regular."),
                AlertAnnotation("12 Oct 2026, 10:30", "Silvia Díaz", "Presentó un leve temblor en las manos, se le dio su té tranquilizante."),
                AlertAnnotation("12 Oct 2026, 15:00", "Tú", "Realizó sus ejercicios de fisioterapia de piernas sin complicaciones.")
            )
        )

        _uiState.update {
            it.copy(alerts = mockAlerts, patientAnnotations = annotations)
        }
    }
}
