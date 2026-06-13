package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
    val isDeleted: Boolean = false
)

class AbuelitoDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AbuelitoDetailUiState())
    val uiState: StateFlow<AbuelitoDetailUiState> = _uiState.asStateFlow()

    // Cargar datos mock según el id del abuelito
    fun loadPatient(patientId: Long) {
        if (_uiState.value.patientId != 0L) return // Evitar recarga si ya tiene datos

        val patientName = when (patientId) {
            1L -> "Carmen Rosa"
            2L -> "Don Roberto"
            3L -> "Elena Soto"
            else -> "Carmen Rosa"
        }
        val patientEdad = when (patientId) {
            1L -> "85"
            2L -> "82"
            3L -> "79"
            else -> "85"
        }
        val patientGrupo = when (patientId) {
            1L -> "O+"
            2L -> "A+"
            3L -> "B-"
            else -> "O+"
        }
        val patientDni = when (patientId) {
            1L -> "17708023"
            2L -> "09871234"
            3L -> "23456789"
            else -> "17708023"
        }
        val patientEnfermedades = when (patientId) {
            1L -> "Hipertensión"
            2L -> "Artrosis"
            3L -> "Diabetes"
            else -> "Hipertensión"
        }
        val patientMedicamentos = when (patientId) {
            1L -> "Losartán 50mg"
            2L -> "Paracetamol"
            3L -> "Metformina"
            else -> "Losartán 50mg"
        }

        _uiState.update {
            it.copy(
                patientId = patientId,
                nombre = patientName,
                edad = patientEdad,
                grupoSanguineo = patientGrupo,
                dni = patientDni,
                enfermedades = patientEnfermedades,
                medicamentos = patientMedicamentos
            )
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
        // Restaurar valores anteriores al forzar recarga
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
        _uiState.update { state ->
            val finalState = validateFields(state)
            if (finalState.isFormValid) {
                finalState.copy(isSaved = true, isEditMode = false)
            } else {
                finalState
            }
        }
    }

    fun resetSavedState() {
        _uiState.update { it.copy(isSaved = false) }
    }

    fun deletePatient() {
        _uiState.update { it.copy(isDeleted = true) }
    }
}
