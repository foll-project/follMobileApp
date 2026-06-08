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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.navigation.Routes
import pe.edu.upc.follmobileapp.core.ui.components.FollTextField
import pe.edu.upc.follmobileapp.core.ui.theme.FollDarkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val backgroundGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2), Color(0xFFFDF1), Color(0xFFFDF1), Color(0xFFFDF1))
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
                    FollTextField(label = "Nombre", placeholder = "Nombre", value = nombre, onValueChange = { nombre = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(label = "Correo", placeholder = "Correo", value = correo, onValueChange = { correo = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(label = "Celular", placeholder = "Celular", value = celular, onValueChange = { celular = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(label = "DNI", placeholder = "DNI", value = dni, onValueChange = { dni = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(label = "Contraseña", placeholder = "Contraseña", value = password, onValueChange = { password = it }, isPassword = true)
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(label = "Confirmar Contraseña", placeholder = "Contraseña", value = confirmPassword, onValueChange = { confirmPassword = it }, isPassword = true)

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text("Crear Cuenta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }

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
                            modifier = Modifier.clickable { navController.navigate(Routes.Login.route) }
                        )
                    }
                }
            }
        }
    }
}