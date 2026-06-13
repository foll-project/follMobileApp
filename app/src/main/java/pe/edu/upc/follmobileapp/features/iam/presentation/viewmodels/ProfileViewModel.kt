package pe.edu.upc.follmobileapp.features.iam.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ProfileSubScreen {
    MAIN,
    UPDATE_DATA,
    HELP_SUPPORT
}

data class ProfileUiState(
    val currentSubScreen: ProfileSubScreen = ProfileSubScreen.MAIN,
    
    // User fields (initial values mock what is in ProfileScreen: María González)
    val firstName: String = "María",
    val firstNameError: String? = null,
    val lastName: String = "González",
    val lastNameError: String? = null,
    val email: String = "maria.gonzalez@gmail.com",
    val emailError: String? = null,
    val phoneNumber: String = "987654321",
    val phoneNumberError: String? = null,
    val password: String = "maria123",
    val passwordError: String? = null,
    val confirmPassword: String = "maria123",
    val confirmPasswordError: String? = null,
    
    val actionMessage: String? = null
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun navigateTo(subScreen: ProfileSubScreen) {
        _uiState.update { it.copy(currentSubScreen = subScreen) }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun hasDigit(text: String): Boolean {
        return text.any { it.isDigit() }
    }

    private fun hasLetter(text: String): Boolean {
        return text.any { it.isLetter() }
    }

    fun onFirstNameChanged(value: String) {
        _uiState.update { state ->
            val err = when {
                value.isBlank() -> "El nombre es obligatorio"
                hasDigit(value) -> "El nombre no puede contener números"
                else -> null
            }
            state.copy(firstName = value, firstNameError = err)
        }
    }

    fun onLastNameChanged(value: String) {
        _uiState.update { state ->
            val err = when {
                value.isBlank() -> "El apellido es obligatorio"
                hasDigit(value) -> "El apellido no puede contener números"
                else -> null
            }
            state.copy(lastName = value, lastNameError = err)
        }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { state ->
            val err = when {
                value.isBlank() -> "El correo electrónico es obligatorio"
                !isEmailValid(value) -> "El correo electrónico debe tener un formato válido"
                else -> null
            }
            state.copy(email = value, emailError = err)
        }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update { state ->
            val err = when {
                value.isBlank() -> "El celular es obligatorio"
                hasLetter(value) -> "El celular no puede contener letras"
                else -> null
            }
            state.copy(phoneNumber = value, phoneNumberError = err)
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { state ->
            state.copy(
                password = value,
                passwordError = if (value.isBlank()) "La contraseña es obligatoria" else null
            )
        }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { state ->
            state.copy(
                confirmPassword = value,
                confirmPasswordError = when {
                    value.isBlank() -> "Confirmar contraseña es obligatorio"
                    value != state.password -> "Las contraseñas no coinciden"
                    else -> null
                }
            )
        }
    }

    fun updateProfile(): Boolean {
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
                !isEmailValid(state.email) -> { isValid = false; "El correo electrónico debe tener un formato válido" }
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
                confirmPasswordError = confirmPasswordErr,
                actionMessage = if (isValid) "Datos actualizados correctamente" else null,
                currentSubScreen = if (isValid) ProfileSubScreen.MAIN else state.currentSubScreen
            )
        }
        return isValid
    }

    fun clearActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }
}
