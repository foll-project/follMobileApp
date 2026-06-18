package pe.edu.upc.follmobileapp.features.iam.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.navigation.Routes
import pe.edu.upc.follmobileapp.core.ui.components.FollButton
import pe.edu.upc.follmobileapp.core.ui.components.FollTextField
import pe.edu.upc.follmobileapp.core.ui.theme.FollDarkBlue
import pe.edu.upc.follmobileapp.features.iam.data.di.DataModule
import pe.edu.upc.follmobileapp.features.iam.presentation.viewmodels.RegisterViewModel
import pe.edu.upc.follmobileapp.features.iam.presentation.viewmodels.RegisterViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(DataModule.provideAuthRepository(context))
    )
    val uiState by viewModel.uiState.collectAsState()

    val backgroundGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2), Color(0xFFFFFDF1), Color(0xFFFFFDF1), Color(0xFFFFFDF1))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 48.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = FollDarkBlue
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                shape = RoundedCornerShape(40.dp),
                color = Color(0xFBFFFFFF), // Blanco sólido
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 40.dp)
                ) {
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    FollTextField(
                        label = "Nombres",
                        placeholder = "Nombres",
                        value = uiState.firstName,
                        onValueChange = viewModel::onFirstNameChanged,
                        isError = uiState.firstNameError != null,
                        errorMessage = uiState.firstNameError,
                        enabled = !uiState.isLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(
                        label = "Apellidos",
                        placeholder = "Apellidos",
                        value = uiState.lastName,
                        onValueChange = viewModel::onLastNameChanged,
                        isError = uiState.lastNameError != null,
                        errorMessage = uiState.lastNameError,
                        enabled = !uiState.isLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(
                        label = "Correo",
                        placeholder = "Correo",
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChanged,
                        isError = uiState.emailError != null,
                        errorMessage = uiState.emailError,
                        enabled = !uiState.isLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(
                        label = "Celular",
                        placeholder = "Celular",
                        value = uiState.phoneNumber,
                        onValueChange = viewModel::onPhoneChanged,
                        isError = uiState.phoneNumberError != null,
                        errorMessage = uiState.phoneNumberError,
                        enabled = !uiState.isLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(
                        label = "Contraseña",
                        placeholder = "Contraseña",
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChanged,
                        isPassword = true,
                        isError = uiState.passwordError != null,
                        errorMessage = uiState.passwordError,
                        enabled = !uiState.isLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(
                        label = "Confirmar Contraseña",
                        placeholder = "Contraseña",
                        value = uiState.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChanged,
                        isPassword = true,
                        isError = uiState.confirmPasswordError != null,
                        errorMessage = uiState.confirmPasswordError,
                        enabled = !uiState.isLoading
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    FollButton(
                        text = if (uiState.isLoading) "Registrando..." else "Crear Cuenta",
                        enabled = !uiState.isLoading,
                        onClick = {
                            viewModel.register {
                                navController.navigate(Routes.Login.route) {
                                    popUpTo(Routes.Welcome.route) { inclusive = false }
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Ya tienes cuenta? ", color = FollDarkBlue, fontSize = 14.sp)
                        Text(
                            text = "Inicia sesión",
                            color = FollDarkBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable(enabled = !uiState.isLoading) {
                                navController.navigate(Routes.Login.route) {
                                    popUpTo(Routes.Welcome.route) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}