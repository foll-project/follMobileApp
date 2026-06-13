package pe.edu.upc.follmobileapp.features.iam.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterUiState(
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null
)

class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun hasDigit(text: String): Boolean {
        return text.any { it.isDigit() }
    }

    private fun hasLetter(text: String): Boolean {
        return text.any { it.isLetter() }
    }

    private fun validatePrecedingFields(index: Int, state: RegisterUiState): RegisterUiState {
        var updated = state
        if (index > 1) {
            val err = when {
                state.firstName.isBlank() -> "El nombre es obligatorio"
                hasDigit(state.firstName) -> "El nombre no puede contener números"
                else -> null
            }
            updated = updated.copy(firstNameError = err)
        }
        if (index > 2) {
            val err = when {
                state.lastName.isBlank() -> "El apellido es obligatorio"
                hasDigit(state.lastName) -> "El apellido no puede contener números"
                else -> null
            }
            updated = updated.copy(lastNameError = err)
        }
        if (index > 3) {
            val err = when {
                state.email.isBlank() -> "El correo electrónico es obligatorio"
                !isEmailValid(state.email) -> "El correo electrónico debe tener un formato válido (ej. usuario@dominio.com)"
                else -> null
            }
            updated = updated.copy(emailError = err)
        }
        if (index > 4) {
            val err = when {
                state.phoneNumber.isBlank() -> "El celular es obligatorio"
                hasLetter(state.phoneNumber) -> "El celular no puede contener letras"
                else -> null
            }
            updated = updated.copy(phoneNumberError = err)
        }
        if (index > 5) {
            val err = when {
                state.password.isBlank() -> "La contraseña es obligatoria"
                else -> null
            }
            updated = updated.copy(passwordError = err)
        }
        return updated
    }

    fun onFirstNameChanged(value: String) {
        _uiState.update { state ->
            val err = when {
                value.isBlank() -> "El nombre es obligatorio"
                hasDigit(value) -> "El nombre no puede contener números"
                else -> null
            }
            state.copy(
                firstName = value,
                firstNameError = err
            )
        }
    }

    fun onLastNameChanged(value: String) {
        _uiState.update { state ->
            val err = when {
                value.isBlank() -> "El apellido es obligatorio"
                hasDigit(value) -> "El apellido no puede contener números"
                else -> null
            }
            val step1 = state.copy(
                lastName = value,
                lastNameError = err
            )
            validatePrecedingFields(2, step1)
        }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { state ->
            val err = when {
                value.isBlank() -> "El correo electrónico es obligatorio"
                !isEmailValid(value) -> "El correo electrónico debe tener un formato válido (ej. usuario@dominio.com)"
                else -> null
            }
            val step1 = state.copy(
                email = value,
                emailError = err
            )
            validatePrecedingFields(3, step1)
        }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update { state ->
            val err = when {
                value.isBlank() -> "El celular es obligatorio"
                hasLetter(value) -> "El celular no puede contener letras"
                else -> null
            }
            val step1 = state.copy(
                phoneNumber = value,
                phoneNumberError = err
            )
            validatePrecedingFields(4, step1)
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { state ->
            val step1 = state.copy(
                password = value,
                passwordError = if (value.isBlank()) "La contraseña es obligatoria" else null
            )
            val step2 = validatePrecedingFields(5, step1)
            if (step2.confirmPassword.isNotEmpty()) {
                val match = value == step2.confirmPassword
                step2.copy(confirmPasswordError = if (match) null else "Las contraseñas no coinciden")
            } else {
                step2
            }
        }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { state ->
            val step1 = state.copy(
                confirmPassword = value
            )
            val step2 = validatePrecedingFields(6, step1)
            val passwordsMatch = step2.password == value
            step2.copy(
                confirmPasswordError = if (value.isBlank()) {
                    "Confirmar contraseña es obligatorio"
                } else if (!passwordsMatch) {
                    "Las contraseñas no coinciden"
                } else {
                    null
                }
            )
        }
    }

    fun validate(): Boolean {
        var isValid = true
        _uiState.update { state ->
            val firstNameErr = when {
                state.firstName.isBlank() -> { isValid = false; "El nombre es obligatorio" }
                hasDigit(state.firstName) -> { isValid = false; "El nombre no puede contener números" }
                else -> null
            }
            val lastNameErr = when {
                state.lastName.isBlank() -> { isValid = false; "El apellido es obligatorio" }
                hasDigit(state.lastName) -> { isValid = false; "El apellido no puede contener números" }
                else -> null
            }
            val emailErr = when {
                state.email.isBlank() -> { isValid = false; "El correo electrónico es obligatorio" }
                !isEmailValid(state.email) -> { isValid = false; "El correo electrónico debe tener un formato válido (ej. usuario@dominio.com)" }
                else -> null
            }
            val phoneErr = when {
                state.phoneNumber.isBlank() -> { isValid = false; "El celular es obligatorio" }
                hasLetter(state.phoneNumber) -> { isValid = false; "El celular no puede contener letras" }
                else -> null
            }
            val passwordErr = when {
                state.password.isBlank() -> { isValid = false; "La contraseña es obligatoria" }
                else -> null
            }
            val confirmPasswordErr = when {
                state.confirmPassword.isBlank() -> { isValid = false; "Confirmar contraseña es obligatorio" }
                state.password != state.confirmPassword -> { isValid = false; "Las contraseñas no coinciden" }
                else -> null
            }

            state.copy(
                firstNameError = firstNameErr,
                lastNameError = lastNameErr,
                emailError = emailErr,
                phoneNumberError = phoneErr,
                passwordError = passwordErr,
                confirmPasswordError = confirmPasswordErr
            )
        }
        return isValid
    }
}
