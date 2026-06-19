package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.edu.upc.follmobileapp.features.care.domain.repository.PatientRepository
import pe.edu.upc.follmobileapp.features.care.domain.models.DeviceInfo
import pe.edu.upc.follmobileapp.features.devicemanagement.domain.repository.DeviceRepository

data class AbuelitoDetailUiState(
    val patientId: Long = 0L,
    val nombre: String = "",
    val nombreError: String? = null,
    val edad: String = "",
    val edadError: String? = null,
    val grupoSanguineo: String = "",
    val grupoSanguineoError: String? = null,
    val dni: String = "",
    val dniError: String? = null,
    val enfermedades: String = "",
    val enfermedadesError: String? = null,
    val medicamentos: String = "",
    val isEditMode: Boolean = false,
    val isFormValid: Boolean = true,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val device: DeviceInfo? = null,
    val actionMessage: String? = null
)

class AbuelitoDetailViewModel(
    private val patientRepository: PatientRepository,
    private val deviceRepository: DeviceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AbuelitoDetailUiState())
    val uiState: StateFlow<AbuelitoDetailUiState> = _uiState.asStateFlow()

    private var patientObserveJob: kotlinx.coroutines.Job? = null

    fun loadPatient(patientId: Long) {
        if (_uiState.value.patientId == patientId) return

        patientObserveJob?.cancel()
        patientObserveJob = viewModelScope.launch {
            patientRepository.getPatientByIdFlow(patientId).collect { patient ->
                if (patient != null) {
                    _uiState.update { state ->
                        if (!state.isEditMode) {
                            state.copy(
                                patientId = patientId,
                                nombre = patient.fullName,
                                edad = patient.age,
                                grupoSanguineo = patient.bloodType,
                                dni = patient.dni,
                                enfermedades = patient.illnesses.joinToString(", "),
                                medicamentos = patient.medications.joinToString(", "),
                                device = patient.device
                            )
                        } else {
                            state.copy(
                                patientId = patientId,
                                device = patient.device
                            )
                        }
                    }
                }
            }
        }

        // Sync from server
        viewModelScope.launch {
            patientRepository.syncPatientDetails(patientId)
        }
    }

    fun toggleEditMode() {
        _uiState.update { state ->
            state.copy(isEditMode = !state.isEditMode)
        }
    }

    fun cancelEdit() {
        _uiState.update { state ->
            state.copy(isEditMode = false)
        }
        val originalId = _uiState.value.patientId
        _uiState.update { it.copy(patientId = 0L) }
        loadPatient(originalId)
    }

    fun onNombreChange(newValue: String) {
        val filtered = newValue.filter { !it.isDigit() }
        _uiState.update { state ->
            val newState = state.copy(nombre = filtered)
            validateFields(newState)
        }
    }

    fun onEdadChange(newValue: String) {
        val filtered = newValue.filter { it.isDigit() }
        _uiState.update { state ->
            val newState = state.copy(edad = filtered)
            validateFields(newState)
        }
    }

    fun onGrupoSanguineoChange(newValue: String) {
        _uiState.update { state ->
            val newState = state.copy(grupoSanguineo = newValue)
            validateFields(newState)
        }
    }

    fun onDniChange(newValue: String) {
        val filtered = newValue.filter { it.isDigit() }.take(8)
        _uiState.update { state ->
            val newState = state.copy(dni = filtered)
            validateFields(newState)
        }
    }

    fun onEnfermedadesChange(newValue: String) {
        _uiState.update { state ->
            val newState = state.copy(enfermedades = newValue)
            validateFields(newState)
        }
    }

    fun onMedicamentosChange(newValue: String) {
        _uiState.update { state ->
            state.copy(medicamentos = newValue)
        }
    }

    private fun validateFields(state: AbuelitoDetailUiState): AbuelitoDetailUiState {
        val nombreErr = if (state.nombre.isBlank()) "El nombre es obligatorio" else null
        val edadErr = if (state.edad.isBlank()) "La edad es obligatoria" else null
        val grupoSanguineoErr = if (state.grupoSanguineo.isBlank()) "El grupo sanguíneo es obligatorio" else null
        val dniErr = if (state.dni.isBlank()) "El DNI es obligatorio" else if (state.dni.length != 8) "El DNI debe tener 8 dígitos" else null
        val enfermedadesErr = if (state.enfermedades.isBlank()) "Las condiciones médicas son obligatorias" else null

        val isValid = nombreErr == null && edadErr == null && grupoSanguineoErr == null &&
                dniErr == null && enfermedadesErr == null

        return state.copy(
            nombreError = nombreErr,
            edadError = edadErr,
            grupoSanguineoError = grupoSanguineoErr,
            dniError = dniErr,
            enfermedadesError = enfermedadesErr,
            isFormValid = isValid
        )
    }

    fun savePatient() {
        val state = _uiState.value
        val finalState = validateFields(state)
        if (!finalState.isFormValid) {
            _uiState.value = finalState
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val illnessesList = state.enfermedades.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val medicationsList = state.medicamentos.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            val result = patientRepository.updatePatient(
                id = state.patientId,
                name = state.nombre,
                bloodType = state.grupoSanguineo,
                illnesses = illnessesList,
                medications = medicationsList
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(isSaved = true, isEditMode = false, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al guardar los cambios en el servidor") }
            }
        }
    }

    fun resetSavedState() {
        _uiState.update { it.copy(isSaved = false) }
    }

    fun deletePatient() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = patientRepository.deletePatientLocally(_uiState.value.patientId)
            if (result.isSuccess) {
                _uiState.update { it.copy(isDeleted = true, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al eliminar localmente") }
            }
        }
    }

    fun linkDevice(deviceId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = deviceRepository.linkDevice(deviceId, _uiState.value.patientId)
            if (result.isSuccess) {
                patientRepository.syncPatientDetails(_uiState.value.patientId)
                _uiState.update { it.copy(isLoading = false, actionMessage = "Dispositivo vinculado correctamente") }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al vincular el dispositivo") }
            }
        }
    }

    fun unlinkDevice(deviceId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = deviceRepository.unlinkDevice(deviceId)
            if (result.isSuccess) {
                patientRepository.syncPatientDetails(_uiState.value.patientId)
                _uiState.update { it.copy(isLoading = false, actionMessage = "Dispositivo desvinculado correctamente") }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al desvincular el dispositivo") }
            }
        }
    }

    fun clearActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
