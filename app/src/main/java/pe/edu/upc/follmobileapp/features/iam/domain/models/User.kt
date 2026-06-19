package pe.edu.upc.follmobileapp.features.iam.domain.models

data class User(
    val userId: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val token: String
)
