package pe.edu.upc.follmobileapp.features.care.data.remote.models

data class CreatePatientRequest(
    val dni: String,
    val firstName: String,
    val lastName: String,
    val birthDate: String = "1950-01-01",
    val relationshipTypeId: Int = 1,
    val bloodType: Int,
    val medicalConditions: Map<String, String>,
    val medications: Map<String, String>
)
