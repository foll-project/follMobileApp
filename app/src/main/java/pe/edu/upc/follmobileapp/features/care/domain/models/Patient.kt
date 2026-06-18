package pe.edu.upc.follmobileapp.features.care.domain.models

data class Patient(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val dni: String,
    val birthDate: String,
    val bloodType: String,
    val illnesses: List<String>,
    val medications: List<String>,
    val caregivers: List<Caregiver> = emptyList(),
    val annotations: List<Annotation> = emptyList(),
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val device: DeviceInfo? = null,
    val caregiverKind: String = "caregiver"
) {
    val fullName: String
        get() = "$firstName $lastName".trim().ifEmpty { "Sin nombre" }
        
    val age: String
        get() = calcularEdad(birthDate)

    companion object {
        fun calcularEdad(birthDateString: String): String {
            if (birthDateString.isBlank()) return "--"
            try {
                // simple birthDate format parsing: yyyy-MM-dd
                val parts = birthDateString.split("-")
                if (parts.size >= 3) {
                    val year = parts[0].toIntOrNull() ?: return "--"
                    val month = parts[1].toIntOrNull() ?: return "--"
                    val day = parts[2].substringBefore("T").toIntOrNull() ?: return "--"
                    
                    val today = java.util.Calendar.getInstance()
                    val birth = java.util.Calendar.getInstance().apply {
                        set(year, month - 1, day)
                    }
                    var age = today.get(java.util.Calendar.YEAR) - birth.get(java.util.Calendar.YEAR)
                    if (today.get(java.util.Calendar.DAY_OF_YEAR) < birth.get(java.util.Calendar.DAY_OF_YEAR)) {
                        age--
                    }
                    return age.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "--"
        }
    }
}
