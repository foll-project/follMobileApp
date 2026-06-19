package pe.edu.upc.follmobileapp.features.iam.data.remote.models

data class AuthResponseDto(
    val userId: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val token: String
)
