package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CrearAbuelitoUiState(
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
    val isFormValid: Boolean = false,
    val isSaved: Boolean = false
)

class CrearAbuelitoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CrearAbuelitoUiState())
    val uiState: StateFlow<CrearAbuelitoUiState> = _uiState.asStateFlow()

    fun onNombreChange(newValue: String) {
        // Nombres no pueden tener números
        val filtered = newValue.filter { !it.isDigit() }
        _uiState.update { state ->
            val newState = state.copy(nombre = filtered)
            validateFieldCascade(newState, "nombre")
        }
    }

    fun onEdadChange(newValue: String) {
        // Edad no puede tener letras, solo números
        val filtered = newValue.filter { it.isDigit() }
        _uiState.update { state ->
            val newState = state.copy(edad = filtered)
            validateFieldCascade(newState, "edad")
        }
    }

    fun onGrupoSanguineoChange(newValue: String) {
        _uiState.update { state ->
            val newState = state.copy(grupoSanguineo = newValue)
            validateFieldCascade(newState, "grupoSanguineo")
        }
    }

    fun onDniChange(newValue: String) {
        // DNI no puede tener letras, solo números (máx 8 dígitos en Perú)
        val filtered = newValue.filter { it.isDigit() }.take(8)
        _uiState.update { state ->
            val newState = state.copy(dni = filtered)
            validateFieldCascade(newState, "dni")
        }
    }

    fun onEnfermedadesChange(newValue: String) {
        _uiState.update { state ->
            val newState = state.copy(enfermedades = newValue)
            validateFieldCascade(newState, "enfermedades")
        }
    }

    fun onMedicamentosChange(newValue: String) {
        _uiState.update { state ->
            state.copy(medicamentos = newValue)
        }
    }

    private fun validateFieldCascade(state: CrearAbuelitoUiState, triggerField: String): CrearAbuelitoUiState {
        var nombreErr: String? = state.nombreError
        var edadErr: String? = state.edadError
        var grupoSanguineoErr: String? = state.grupoSanguineoError
        var dniErr: String? = state.dniError
        var enfermedadesErr: String? = state.enfermedadesError

        val isNombreDirty = triggerField == "nombre" || state.nombre.isNotEmpty() || state.nombreError != null
        val isEdadDirty = triggerField == "edad" || state.edad.isNotEmpty() || state.edadError != null || isNombreDirty
        val isGrupoDirty = triggerField == "grupoSanguineo" || state.grupoSanguineo.isNotEmpty() || state.grupoSanguineoError != null || isEdadDirty
        val isDniDirty = triggerField == "dni" || state.dni.isNotEmpty() || state.dniError != null || isGrupoDirty
        val isEnfermedadesDirty = triggerField == "enfermedades" || state.enfermedades.isNotEmpty() || state.enfermedadesError != null || isDniDirty

        if (isNombreDirty) {
            nombreErr = when {
                state.nombre.isBlank() -> "El nombre es obligatorio"
                else -> null
            }
        }
        if (isEdadDirty) {
            edadErr = when {
                state.edad.isBlank() -> "La edad es obligatoria"
                state.edad.toIntOrNull() == null -> "La edad debe ser un número"
                else -> null
            }
        }
        if (isGrupoDirty) {
            grupoSanguineoErr = when {
                state.grupoSanguineo.isBlank() -> "El grupo sanguíneo es obligatorio"
                else -> null
            }
        }
        if (isDniDirty) {
            dniErr = when {
                state.dni.isBlank() -> "El DNI es obligatorio"
                state.dni.length != 8 -> "El DNI debe tener 8 dígitos"
                else -> null
            }
        }
        if (isEnfermedadesDirty) {
            enfermedadesErr = when {
                state.enfermedades.isBlank() -> "Las condiciones médicas son obligatorias"
                else -> null
            }
        }

        val isValid = nombreErr == null && edadErr == null && grupoSanguineoErr == null &&
                dniErr == null && enfermedadesErr == null &&
                state.nombre.isNotBlank() && state.edad.isNotBlank() &&
                state.grupoSanguineo.isNotBlank() && state.dni.isNotBlank() &&
                state.enfermedades.isNotBlank()

        return state.copy(
            nombreError = nombreErr,
            edadError = edadErr,
            grupoSanguineoError = grupoSanguineoErr,
            dniError = dniErr,
            enfermedadesError = enfermedadesErr,
            isFormValid = isValid
        )
    }

    fun saveAbuelito() {
        _uiState.update { state ->
            val finalState = validateAll(state)
            if (finalState.isFormValid) {
                finalState.copy(isSaved = true)
            } else {
                finalState
            }
        }
    }

    private fun validateAll(state: CrearAbuelitoUiState): CrearAbuelitoUiState {
        val nombreErr = if (state.nombre.isBlank()) "El nombre es obligatorio" else null
        val edadErr = if (state.edad.isBlank()) "La edad es obligatoria" else if (state.edad.toIntOrNull() == null) "La edad debe ser un número" else null
        val grupoSanguineoErr = if (state.grupoSanguineo.isBlank()) "El grupo sanguíneo es obligatorio" else null
        val dniErr = if (state.dni.isBlank()) "El DNI es obligatorio" else if (state.dni.length != 8) "El DNI debe tener 8 dígitos" else null
        val enfermedadesErr = if (state.enfermedades.isBlank()) "Las condiciones médicas son obligatorias" else null

        val isValid = nombreErr == null && edadErr == null && grupoSanguineoErr == null && dniErr == null && enfermedadesErr == null

        return state.copy(
            nombreError = nombreErr,
            edadError = edadErr,
            grupoSanguineoError = grupoSanguineoErr,
            dniError = dniErr,
            enfermedadesError = enfermedadesErr,
            isFormValid = isValid
        )
    }
}
