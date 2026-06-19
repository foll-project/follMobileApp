package pe.edu.upc.follmobileapp.features.iam.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val token: String
)
