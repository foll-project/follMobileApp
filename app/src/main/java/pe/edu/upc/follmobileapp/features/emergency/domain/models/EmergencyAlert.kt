package pe.edu.upc.follmobileapp.features.emergency.domain.models

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
