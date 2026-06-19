package pe.edu.upc.follmobileapp.features.care.data.remote.models

data class UpdatePatientRequest(
    val firstName: String,
    val lastName: String,
    val birthDate: String = "1950-01-01",
    val bloodType: Int,
    val medicalConditions: Map<String, String>,
    val medications: Map<String, String>
)
