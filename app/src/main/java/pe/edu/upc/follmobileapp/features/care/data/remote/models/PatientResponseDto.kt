package pe.edu.upc.follmobileapp.features.care.data.remote.models

data class PatientResponseDto(
    val patientId: Long?,
    val firstName: String?,
    val lastName: String?,
    val dni: String?,
    val birthDate: String?,
    val bloodType: Int?,
    val medicalConditions: Map<String, String>?,
    val medications: Map<String, String>?,
    val caregivers: List<CaregiverDto>?,
    val annotations: List<AnnotationDto>?,
    val device: DeviceDto?,
    val emergencyContacts: List<EmergencyContactDto>?,
    val currentGuardianUserId: Int?,
    val officialGuardianUserId: Int?,
    
    // In case details are wrapped
    val patient: PatientResponseDto?,
    val caregiverKind: String?
)

data class EmergencyContactDto(
    val id: Long?,
    val name: String?,
    val phoneNumber: String?,
    val relationship: String?
)

data class CaregiverDto(
    val userId: Int?,
    val user: UserDto?,
    val caregiverKind: String?
)

data class UserDto(
    val firstName: String?,
    val lastName: String?,
    val email: String?
)

data class AnnotationDto(
    val id: Int?,
    val date: String?,
    val text: String?,
    val author: String?
)

data class DeviceDto(
    val isLinked: Boolean?,
    val deviceId: Int?,
    val status: String?,
    val connectivityStatus: String?,
    val currentBatteryLevel: Int?,
    val isCharging: Boolean?,
    val lastHeartbeatAt: String?,
    val isOnline: Boolean?,
    val isLowBattery: Boolean?,
    val firmwareVersion: String?
)
