package pe.edu.upc.follmobileapp.features.iam.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    fun onEmailChanged(email: String) {
        _uiState.update { state ->
            val err = when {
                email.isBlank() -> "El correo electrónico es obligatorio"
                !isEmailValid(email) -> "El correo electrónico debe tener un formato válido (ej. usuario@dominio.com)"
                else -> null
            }
            state.copy(email = email, emailError = err)
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { state ->
            val err = if (password.isBlank()) "La contraseña es obligatoria" else null
            state.copy(password = password, passwordError = err)
        }
    }

    fun validate(): Boolean {
        var isValid = true
        _uiState.update { state ->
            val emailErr = when {
                state.email.isBlank() -> { isValid = false; "El correo electrónico es obligatorio" }
                !isEmailValid(state.email) -> { isValid = false; "El correo electrónico debe tener un formato válido (ej. usuario@dominio.com)" }
                else -> null
            }
            val passwordErr = if (state.password.isBlank()) {
                isValid = false
                "La contraseña es obligatoria"
            } else null

            state.copy(
                emailError = emailErr,
                passwordError = passwordErr
            )
        }
        return isValid
    }
}
