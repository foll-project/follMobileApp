package pe.edu.upc.follmobileapp.features.iam.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.ui.components.FollBottomBar
import pe.edu.upc.follmobileapp.core.ui.theme.*

@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        bottomBar = { FollBottomBar(navController, "profile_screen") },
        containerColor = FollBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header: Logo Foll (Alineado a la izquierda)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = FollDarkBlue, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = White, modifier = Modifier.padding(4.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Foll", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Información de Usuario
            Text("María González", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Usuario Activo", fontSize = 16.sp, color = FollDarkBlue, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(40.dp))

            // Lista de Opciones
            ProfileOptionCard(
                icon = Icons.Default.Person,
                iconBgColor = FollLightGreen.copy(alpha = 0.5f),
                title = "Actualizar Mis Datos",
                subtitle = "Información personal y médica"
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProfileOptionCard(
                icon = Icons.Default.Notifications,
                iconBgColor = FollLightGreen,
                title = "Configuración de Notificaciones",
                subtitle = "Alertas, sonidos y avisos"
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProfileOptionCard(
                icon = Icons.Default.HelpOutline,
                iconBgColor = FollYellow,
                title = "Ayuda y Soporte",
                subtitle = "Preguntas frecuentes y contacto"
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón Cerrar Sesión
            TextButton(
                onClick = { /* Lógica de Cerrar Sesión */ },
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = FollDarkBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
            }
        }
    }
}

@Composable
fun ProfileOptionCard(icon: ImageVector, iconBgColor: Color, title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = iconBgColor,
                modifier = Modifier.size(50.dp)
            ) {
                Icon(icon, contentDescription = null, tint = FollDarkBlue, modifier = Modifier.padding(12.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(subtitle, fontSize = 14.sp, color = FollDarkBlue)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = "Ir", tint = Color.LightGray)
        }
    }
}