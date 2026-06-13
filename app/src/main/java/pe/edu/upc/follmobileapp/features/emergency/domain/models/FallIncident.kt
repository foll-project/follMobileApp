package pe.edu.upc.follmobileapp.features.emergency.domain.models

data class FallIncident(
    val id: Long,
    val patientId: Long,
    val patientName: String,
    val isRealEmergency: Boolean,
    val dateString: String,
    val timeString: String,
    val fallType: String,
    val responseTime: String,
    val observations: String,
    val latitude: Double,
    val longitude: Double
)
