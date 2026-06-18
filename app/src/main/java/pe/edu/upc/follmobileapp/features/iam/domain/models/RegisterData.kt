package pe.edu.upc.follmobileapp.features.iam.domain.models

data class RegisterData(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)
