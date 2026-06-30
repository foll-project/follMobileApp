package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.edu.upc.follmobileapp.features.care.domain.models.Patient
import pe.edu.upc.follmobileapp.features.care.domain.repository.PatientRepository
import pe.edu.upc.follmobileapp.features.iam.domain.repository.AuthRepository
import pe.edu.upc.follmobileapp.features.emergency.domain.repository.EmergencyRepository

enum class CaregiverRole(val label: String) {
    OFFICIAL_GUARDIAN("Cuidador Principal"),
    SECONDARY_GUARDIAN("Cuidador Secundario"),
    INVITED_GUARDIAN("Principal Invitado")
}

data class PatientUiModel(
    val id: Long,
    val name: String,
    val role: CaregiverRole,
    val deviceId: String,
    val isDeviceOn: Boolean,
    val batteryPercentage: Int,
    val isCharging: Boolean,
    val isInEmergency: Boolean = false
)

data class CareUiState(
    val patients: List<PatientUiModel> = emptyList(),
    val expandedPatientIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CareViewModel(
    private val patientRepository: PatientRepository,
    private val authRepository: AuthRepository,
    private val emergencyRepository: EmergencyRepository
) : ViewModel() {

    private val _expandedPatientIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CareUiState> = combine(
        patientRepository.getPatientsFlow(),
        emergencyRepository.getAlertsFlow(),
        _expandedPatientIds,
        _isLoading,
        _errorMessage
    ) { patientsList, activeAlerts, expandedIds, isLoading, errorMsg ->
        val emergencyPatientIds = activeAlerts.map { it.patientId }.toSet()
        val uiPatients = patientsList.map { patient ->
            val role = when (patient.caregiverKind) {
                "official" -> CaregiverRole.OFFICIAL_GUARDIAN
                "invited_primary" -> CaregiverRole.INVITED_GUARDIAN
                else -> CaregiverRole.SECONDARY_GUARDIAN
            }
            PatientUiModel(
                id = patient.id,
                name = patient.fullName,
                role = role,
                deviceId = patient.device?.id ?: "Sin dispositivo",
                isDeviceOn = patient.device?.status == "Online",
                batteryPercentage = patient.device?.batteryPercentage ?: 0,
                isCharging = patient.device?.isCharging ?: false,
                isInEmergency = emergencyPatientIds.contains(patient.id)
            )
        }
        CareUiState(
            patients = uiPatients,
            expandedPatientIds = expandedIds,
            isLoading = isLoading,
            errorMessage = errorMsg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CareUiState()
    )

    init {
        syncPatientsFromRemote()
        syncAlertsFromRemote()
    }

    fun syncPatientsFromRemote() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            authRepository.getLoggedInUser().firstOrNull()?.let { user ->
                val result = patientRepository.syncPatients(user.userId)
                if (result.isFailure) {
                    _errorMessage.value = "Error de sincronización con el servidor"
                }
            }
            _isLoading.value = false
        }
    }

    /** Sincroniza alertas de caída al abrir Mis Abuelitos (alinea Room con el backend). */
    fun syncAlertsFromRemote() {
        viewModelScope.launch {
            emergencyRepository.syncAlerts()
        }
    }

    fun togglePatientExpansion(patientId: Long) {
        val current = _expandedPatientIds.value
        _expandedPatientIds.value = if (current.contains(patientId)) {
            current - patientId
        } else {
            current + patientId
        }
    }

    fun vincularCuidadorPorQr(patientId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                authRepository.getLoggedInUser().firstOrNull()?.let { user ->
                    val result = patientRepository.linkCaregiverViaQr(patientId, user.userId.toLong())
                    if (result.isSuccess) {
                        syncPatientsFromRemote()
                    } else {
                        val exception = result.exceptionOrNull()
                        val errorMsg = if (exception is retrofit2.HttpException) {
                            try {
                                val errorBody = exception.response()?.errorBody()?.string()
                                if (errorBody != null && errorBody.contains("message")) {
                                    org.json.JSONObject(errorBody).getString("message")
                                } else {
                                    "Error al vincular: código ${exception.code()}"
                                }
                            } catch (e: Exception) {
                                "Error HTTP ${exception.code()}"
                            }
                        } else {
                            exception?.message ?: "Error desconocido"
                        }
                        _errorMessage.value = errorMsg
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
