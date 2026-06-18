package pe.edu.upc.follmobileapp.features.care.domain.models

data class EmergencyContact(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val relationship: String
)
