package pe.edu.upc.follmobileapp.features.care.data.remote.models

data class CreateEmergencyContactRequest(
    val name: String,
    val phoneNumber: String,
    val relationship: String
)
