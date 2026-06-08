package pe.edu.upc.follmobileapp.features.emergency.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Warning
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
import pe.edu.upc.follmobileapp.core.ui.components.FollBottomBar
import pe.edu.upc.follmobileapp.core.ui.theme.*
import pe.edu.upc.follmobileapp.core.ui.components.FollTopBar

@Composable
fun AlertsScreen(navController: NavController) {
    val backgroundGradient = Brush.linearGradient(colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2), Color(0xFFFDF1), Color(0xFFFDF1), Color(0xFFFDF1)))

    Scaffold(
        bottomBar = { FollBottomBar(navController, "alerts_screen") },
        topBar = { FollTopBar(navController) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Últimas Alertas", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)

                Spacer(modifier = Modifier.height(24.dp))

                // Tarjeta de Alerta (Hacer clic lleva a la pantalla roja)
                AlertaCard(
                    nombre = "Carlos Mendoza",
                    mensaje = "Posible caída detectada hace 1 min",
                    onClick = { navController.navigate(Routes.AlertDetail.route) }
                )
            }
        }
    }
}

@Composable
fun AlertaCard(nombre: String, mensaje: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFBFFFFFF),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de advertencia rojo
            Surface(
                shape = CircleShape,
                color = Color(0xFFA04040), // Rojo oscuro de la alerta
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = "Alerta", tint = Color.White, modifier = Modifier.padding(12.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA04040))
                Spacer(modifier = Modifier.height(4.dp))
                Text(mensaje, fontSize = 14.sp, color = FollDarkBlue)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = "Ver detalle", tint = Color.LightGray)
        }
    }
}