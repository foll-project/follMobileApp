package pe.edu.upc.follmobileapp.features.iam.data.remote.models

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)
