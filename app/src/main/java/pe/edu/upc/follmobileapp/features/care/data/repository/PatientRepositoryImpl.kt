package pe.edu.upc.follmobileapp.features.care.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upc.follmobileapp.features.care.data.local.PatientLocalDataSource
import pe.edu.upc.follmobileapp.features.care.data.remote.models.*
import pe.edu.upc.follmobileapp.features.care.data.remote.services.PatientService
import pe.edu.upc.follmobileapp.features.care.domain.models.Annotation
import pe.edu.upc.follmobileapp.features.care.domain.models.Caregiver
import pe.edu.upc.follmobileapp.features.care.domain.models.DeviceInfo
import pe.edu.upc.follmobileapp.features.care.domain.models.Patient
import pe.edu.upc.follmobileapp.features.care.domain.repository.PatientRepository
import java.text.SimpleDateFormat
import java.util.*

object PatientMapper {
    private val bloodTypes = listOf("Desconocido", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    fun getBloodTypeString(type: Int): String {
        return bloodTypes.getOrNull(type) ?: "Desconocido"
    }

    fun getBloodTypeNumber(typeString: String): Int {
        val index = bloodTypes.indexOf(typeString)
        return if (index != -1) index else 1
    }

    private fun mapDictionary(map: Map<String, String>?): List<String> {
        if (map == null) return emptyList()
        return map.map { (key, value) ->
            if (value.equals("Confirmado", ignoreCase = true) || value.equals("Dosis estándar", ignoreCase = true)) {
                key
            } else {
                value
            }
        }.filter { it.isNotBlank() }
    }

    fun mapAnnotation(ann: AnnotationDto): Annotation {
        val formattedDate = ann.date?.let { dateStr ->
            try {
                val formats = listOf(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    "yyyy-MM-dd'T'HH:mm:ss.SSS",
                    "yyyy-MM-dd'T'HH:mm:ss"
                )
                var parsedDate: Date? = null
                for (fmt in formats) {
                    try {
                        val sdf = SimpleDateFormat(fmt, Locale.US)
                        if (fmt.contains("Z")) {
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                        }
                        parsedDate = sdf.parse(dateStr)
                        if (parsedDate != null) break
                    } catch (e: Exception) {}
                }
                if (parsedDate != null) {
                    val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                    outputFormat.format(parsedDate)
                } else {
                    dateStr
                }
            } catch (e: Exception) {
                dateStr
            }
        } ?: ""
        return Annotation(
            id = ann.id?.toString() ?: UUID.randomUUID().toString(),
            dateString = formattedDate,
            authorName = ann.author ?: "Desconocido",
            content = ann.text ?: ""
        )
    }

    fun toDomain(dto: PatientResponseDto, currentUserId: Int = -1): Patient {
        val inner = dto.patient ?: dto
        
        val caregivers = inner.caregivers?.map { cg ->
            val fullName = "${cg.user?.firstName ?: ""} ${cg.user?.lastName ?: ""}".trim()
            val cgRole = when {
                cg.caregiverKind == "official" || inner.officialGuardianUserId == cg.userId -> "Principal Oficial"
                inner.currentGuardianUserId == cg.userId -> "Principal Invitado"
                else -> "Secundario"
            }
            Caregiver(
                id = cg.userId?.toString() ?: "",
                name = if (fullName.isEmpty()) "Desconocido" else fullName,
                role = cgRole,
                email = cg.user?.email ?: ""
            )
        } ?: emptyList()

        val annotations = inner.annotations?.map { mapAnnotation(it) } ?: emptyList()

        val device = inner.device?.let { dev ->
            val isLinked = dev.isLinked == true
            if (isLinked) {
                val battery = dev.currentBatteryLevel ?: 0
                val charging = dev.isCharging == true
                val status = if (dev.isOnline == true) "Online" else "Offline"
                val reportTime = dev.lastHeartbeatAt?.let { heartbeat ->
                    formatUltimoReporte(heartbeat)
                } ?: "Sin reportes"
                
                DeviceInfo(
                    id = if (dev.deviceId != null) "#${dev.deviceId}" else "N/D",
                    batteryPercentage = battery,
                    isCharging = charging,
                    status = status,
                    ultimoReporte = reportTime
                )
            } else null
        }

        val emergencyContacts = inner.emergencyContacts?.map { ec ->
            pe.edu.upc.follmobileapp.features.care.domain.models.EmergencyContact(
                id = ec.id ?: 0L,
                name = ec.name ?: "",
                phoneNumber = ec.phoneNumber ?: "",
                relationship = ec.relationship ?: ""
            )
        } ?: emptyList()

        // Determinar el rol del usuario logueado respecto a este paciente
        val determinedKind = when {
            dto.caregiverKind == "official" || inner.officialGuardianUserId == currentUserId -> "official"
            inner.currentGuardianUserId == currentUserId -> "invited_primary"
            dto.caregiverKind != null -> dto.caregiverKind // si viene "caregiver" o null del wrapper
            else -> inner.caregiverKind ?: "caregiver"
        }

        return Patient(
            id = inner.patientId ?: 0L,
            firstName = inner.firstName ?: "",
            lastName = inner.lastName ?: "",
            dni = inner.dni ?: "",
            birthDate = inner.birthDate ?: "1950-01-01",
            bloodType = getBloodTypeString(inner.bloodType ?: 0),
            illnesses = mapDictionary(inner.medicalConditions),
            medications = mapDictionary(inner.medications),
            caregivers = caregivers,
            annotations = annotations,
            emergencyContacts = emergencyContacts,
            device = device,
            caregiverKind = determinedKind
        )
    }

    fun formatUltimoReporte(fecha: String): String {
        try {
            val formats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss"
            )
            var date: Date? = null
            for (fmt in formats) {
                try {
                    val sdf = SimpleDateFormat(fmt, Locale.US)
                    if (fmt.contains("Z")) {
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                    }
                    date = sdf.parse(fecha)
                    if (date != null) break
                } catch (e: Exception) {}
            }
            if (date == null) return "Sin reportes"
            
            val diffMs = System.currentTimeMillis() - date.time
            val diffMin = diffMs / 60000
            
            if (diffMin < 1) return "Hace instantes"
            if (diffMin < 60) return "Hace $diffMin min"
            val diffHoras = diffMin / 60
            if (diffHoras < 24) return "Hace $diffHoras h"
            
            val outputFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
            return outputFormat.format(date)
        } catch (e: Exception) {
            return "Sin reportes"
        }
    }

    fun toEntity(patient: Patient): pe.edu.upc.follmobileapp.features.care.data.local.models.PatientEntity {
        return pe.edu.upc.follmobileapp.features.care.data.local.models.PatientEntity(
            id = patient.id,
            firstName = patient.firstName,
            lastName = patient.lastName,
            dni = patient.dni,
            birthDate = patient.birthDate,
            bloodType = patient.bloodType,
            illnesses = patient.illnesses,
            medications = patient.medications,
            caregivers = patient.caregivers,
            annotations = patient.annotations,
            emergencyContacts = patient.emergencyContacts,
            device = patient.device,
            caregiverKind = patient.caregiverKind
        )
    }

    fun entityToDomain(entity: pe.edu.upc.follmobileapp.features.care.data.local.models.PatientEntity): Patient {
        return Patient(
            id = entity.id,
            firstName = entity.firstName,
            lastName = entity.lastName,
            dni = entity.dni,
            birthDate = entity.birthDate,
            bloodType = entity.bloodType,
            illnesses = entity.illnesses,
            medications = entity.medications,
            caregivers = entity.caregivers,
            annotations = entity.annotations,
            emergencyContacts = entity.emergencyContacts,
            device = entity.device,
            caregiverKind = entity.caregiverKind
        )
    }
}

class PatientRepositoryImpl(
    private val localDataSource: PatientLocalDataSource,
    private val patientService: PatientService
) : PatientRepository {

    override fun getPatientsFlow(): Flow<List<Patient>> {
        return localDataSource.getPatientsFlow().map { list ->
            list.map { PatientMapper.entityToDomain(it) }
        }
    }

    override fun getPatientByIdFlow(id: Long): Flow<Patient?> {
        return localDataSource.getPatientByIdFlow(id).map { entity ->
            entity?.let { PatientMapper.entityToDomain(it) }
        }
    }

    override suspend fun syncPatients(caregiverUserId: Int): Result<Unit> = runCatching {
        val response = patientService.getPatientsByCaregiver(caregiverUserId)
        val domainPatients = response.map { PatientMapper.toDomain(it, caregiverUserId) }
        val entities = domainPatients.map { PatientMapper.toEntity(it) }
        localDataSource.syncPatientsData(entities)
    }

    override suspend fun syncPatientDetails(patientId: Long): Result<Unit> = runCatching {
        val response = patientService.getPatientById(patientId)
        val domainPatient = PatientMapper.toDomain(response)
        
        // Recuperar el paciente local actual para preservar su rol (caregiverKind)
        // ya que el endpoint getPatientById no incluye el wrapper con esta información.
        val existingPatient = localDataSource.getPatientById(patientId)
        val preservedRole = existingPatient?.caregiverKind ?: domainPatient.caregiverKind
        
        // Aplicar el rol preservado a la entidad antes de guardarla en la caché local
        val entity = PatientMapper.toEntity(domainPatient).copy(caregiverKind = preservedRole)
        localDataSource.savePatient(entity)
    }

    override suspend fun createPatient(
        dni: String,
        name: String,
        bloodType: String,
        illnesses: List<String>,
        medications: List<String>
    ): Result<Unit> = runCatching {
        val nameParts = name.trim().split(" ")
        val firstName = nameParts.firstOrNull() ?: ""
        val lastName = if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else "."
        
        val bloodNum = PatientMapper.getBloodTypeNumber(bloodType)
        
        val medicalConditions = illnesses.associateWith { "Confirmado" }
        val medicationsMap = medications.associateWith { "Dosis estándar" }
        
        val request = CreatePatientRequest(
            dni = dni,
            firstName = firstName,
            lastName = lastName,
            bloodType = bloodNum,
            medicalConditions = medicalConditions,
            medications = medicationsMap
        )
        patientService.createPatient(request)
    }

    override suspend fun updatePatient(
        id: Long,
        name: String,
        bloodType: String,
        illnesses: List<String>,
        medications: List<String>
    ): Result<Unit> = runCatching {
        val nameParts = name.trim().split(" ")
        val firstName = nameParts.firstOrNull() ?: ""
        val lastName = if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else "."
        
        val bloodNum = PatientMapper.getBloodTypeNumber(bloodType)
        
        val medicalConditions = illnesses.associateWith { "Confirmado" }
        val medicationsMap = medications.associateWith { "Dosis estándar" }
        
        val request = UpdatePatientRequest(
            firstName = firstName,
            lastName = lastName,
            bloodType = bloodNum,
            medicalConditions = medicalConditions,
            medications = medicationsMap
        )
        patientService.updatePatient(id, request)
        syncPatientDetails(id)
    }

    override suspend fun deletePatientLocally(id: Long): Result<Unit> = runCatching {
        val response = patientService.deletePatient(id)
        if (response.isSuccessful) {
            localDataSource.deletePatient(id)
        } else {
            throw Exception("Fallo en el servidor: HTTP ${response.code()}")
        }
    }

    override suspend fun updateDeviceTelemetry(
        patientId: Long,
        batteryLevel: Int,
        isCharging: Boolean,
        isOnline: Boolean,
        lastHeartbeatAt: String?
    ): Result<Unit> = runCatching {
        val localPatient = localDataSource.getPatientById(patientId) ?: return@runCatching
        val currentDevice = localPatient.device ?: return@runCatching

        val updatedDevice = currentDevice.copy(
            batteryPercentage = batteryLevel,
            isCharging = isCharging,
            status = if (isOnline) "Online" else "Offline",
            ultimoReporte = lastHeartbeatAt?.let { PatientMapper.formatUltimoReporte(it) } ?: "Hace instantes"
        )

        localDataSource.savePatient(localPatient.copy(device = updatedDevice))
    }

    override suspend fun addAnnotation(patientId: Long, content: String): Result<Unit> = runCatching {
        val request = CreateAnnotationRequest(content)
        patientService.createAnnotation(patientId, request)
        syncAnnotations(patientId)
    }

    override suspend fun getCaregivers(patientId: Long): Result<List<Caregiver>> = runCatching {
        val response = patientService.getCaregivers(patientId)
        response.map { cg ->
            val fullName = "${cg.user?.firstName ?: ""} ${cg.user?.lastName ?: ""}".trim()
            Caregiver(
                id = cg.userId?.toString() ?: "",
                name = if (fullName.isEmpty()) "Desconocido" else fullName,
                role = if (cg.caregiverKind == "official") "Principal" else "Invitado",
                email = cg.user?.email ?: ""
            )
        }
    }

    override suspend fun changeGuardian(patientId: Long, newCurrentGuardianUserId: Int): Result<Unit> = runCatching {
        val request = ChangeGuardianRequest(newCurrentGuardianUserId)
        patientService.changeGuardian(patientId, request)
        syncPatientDetails(patientId)
    }

    override suspend fun restoreGuardian(patientId: Long): Result<Unit> = runCatching {
        patientService.restoreGuardian(patientId)
        syncPatientDetails(patientId)
    }

    override suspend fun createEmergencyContact(
        patientId: Long,
        name: String,
        phoneNumber: String,
        relationship: String
    ): Result<Unit> = runCatching {
        val request = CreateEmergencyContactRequest(name, phoneNumber, relationship)
        patientService.createEmergencyContact(patientId, request)
        syncPatientDetails(patientId)
    }

    override suspend fun deleteEmergencyContact(patientId: Long, contactId: Long): Result<Unit> = runCatching {
        patientService.deleteEmergencyContact(patientId, contactId)
        syncPatientDetails(patientId)
    }

    override suspend fun syncAnnotations(patientId: Long): Result<Unit> = runCatching {
        val response = patientService.getAnnotations(patientId)
        val domainAnnotations = response.map { PatientMapper.mapAnnotation(it) }
        val localPatient = localDataSource.getPatientById(patientId)
        if (localPatient != null) {
            val updatedEntity = localPatient.copy(annotations = domainAnnotations)
            localDataSource.savePatient(updatedEntity)
        } else {
            syncPatientDetails(patientId)
        }
    }
    override suspend fun linkCaregiverViaQr(patientId: Long, caregiverId: Long): Result<Unit> = runCatching {
        val request = LinkCaregiverQrRequest(caregiverId)
        patientService.linkCaregiverViaQr(patientId, request)
    }

    override suspend fun removeCaregiver(patientId: Long, caregiverId: Long): Result<Boolean> = runCatching {
        patientService.removeCaregiver(patientId, caregiverId)
        syncPatientDetails(patientId) // Refresca los datos del paciente para removerlo del backend y local
        true
    }
}
