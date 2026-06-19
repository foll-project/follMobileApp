package pe.edu.upc.follmobileapp.features.care.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import pe.edu.upc.follmobileapp.features.care.domain.models.Caregiver
import pe.edu.upc.follmobileapp.features.care.domain.models.Annotation
import pe.edu.upc.follmobileapp.features.care.domain.models.DeviceInfo
import pe.edu.upc.follmobileapp.features.care.domain.models.EmergencyContact

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey val id: Long,
    val firstName: String,
    val lastName: String,
    val dni: String,
    val birthDate: String,
    val bloodType: String,
    val illnesses: List<String>,
    val medications: List<String>,
    val caregivers: List<Caregiver>,
    val annotations: List<Annotation>,
    val emergencyContacts: List<EmergencyContact>,
    val device: DeviceInfo?,
    val caregiverKind: String
)
