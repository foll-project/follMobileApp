package pe.edu.upc.follmobileapp.features.iam.presentation.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.navigation.Routes
import pe.edu.upc.follmobileapp.core.ui.theme.FollDarkBlue

@Composable
fun WelcomeScreen(navController: NavController) {
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2))
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

            // Simulación del Logo (Círculo azul con ícono médico)
            Surface(
                shape = CircleShape,
                color = FollDarkBlue,
                modifier = Modifier.size(100.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = "Logo Foll",
                    tint = Color.White,
                    modifier = Modifier.padding(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Foll",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = FollDarkBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Seguridad vitalicia a tu alcance",
                fontSize = 16.sp,
                color = FollDarkBlue
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Tarjeta blanca inferior con los botones
            Surface(
                shape = RoundedCornerShape(40.dp),
                color = Color(0xFBFFFFFF), // Blanco sólido
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { navController.navigate(Routes.Login.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { navController.navigate(Routes.Register.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        border = BorderStroke(1.dp, FollDarkBlue),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text("Crear Cuenta", color = FollDarkBlue, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}