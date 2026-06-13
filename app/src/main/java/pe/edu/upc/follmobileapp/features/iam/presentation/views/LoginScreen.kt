package pe.edu.upc.follmobileapp.features.iam.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import pe.edu.upc.follmobileapp.features.iam.presentation.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
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
                .padding(vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido de nuevo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = FollDarkBlue
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                shape = RoundedCornerShape(40.dp),
                color = Color(0xFBFFFFFF),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 40.dp)
                ) {
                    FollTextField(
                        label = "Correo electrónico",
                        placeholder = "Correo electrónico",
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChanged,
                        isError = uiState.emailError != null,
                        errorMessage = uiState.emailError
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(
                        label = "Contraseña",
                        placeholder = "Contraseña",
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChanged,
                        isPassword = true,
                        isError = uiState.passwordError != null,
                        errorMessage = uiState.passwordError
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    FollButton(
                        text = "Iniciar Sesión",
                        onClick = {
                            if (viewModel.validate()) {
                                navController.navigate(Routes.Dashboard.route) {
                                    popUpTo(Routes.Welcome.route) { inclusive = true }
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Todavía no tienes cuenta? ", color = FollDarkBlue, fontSize = 14.sp)
                        Text(
                            text = "Regístrate",
                            color = FollDarkBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                navController.navigate(Routes.Register.route) {
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