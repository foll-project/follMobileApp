package pe.edu.upc.follmobileapp.features.emergency.presentation.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.ui.components.FollBottomBar
import pe.edu.upc.follmobileapp.core.ui.theme.*
import pe.edu.upc.follmobileapp.core.ui.components.FollTopBar

@Composable
fun HistoryScreen(navController: NavController) {
    val backgroundGradient = Brush.linearGradient(colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2), Color(0xFFFDF1), Color(0xFFFDF1), Color(0xFFFDF1)))

    Scaffold(
        bottomBar = { FollBottomBar(navController, "history_screen") },
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
                Text("Registro de Caídas", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Historial cronológico de eventos", fontSize = 16.sp, color = FollDarkBlue)

                Spacer(modifier = Modifier.height(32.dp))

                // Evento 1: Emergencia Real (Expandido)
                TimelineItem(isLast = false) {
                    EmergenciaRealCard()
                }

                // Evento 2: Falso Positivo
                TimelineItem(isLast = false) {
                    FalsoPositivoCard(fecha = "12 Oct", hora = "09:15", nombre = "Elena Soto")
                }

                // Evento 3: Falso Positivo
                TimelineItem(isLast = true) {
                    FalsoPositivoCard(fecha = "10 Oct", hora = "18:40", nombre = "Elena Soto")
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun TimelineItem(isLast: Boolean, content: @Composable () -> Unit) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        // Línea y Punto de la línea de tiempo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            // Punto dorado
            Box(modifier = Modifier.size(12.dp).background(FollOrange, CircleShape))
            // Línea vertical
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(FollOrange.copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Contenido de la tarjeta
        Box(modifier = Modifier.weight(1f).padding(bottom = 24.dp)) {
            content()
        }
    }
}

@Composable
fun EmergenciaRealCard() {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFBFFFFFF),
        border = BorderStroke(1.dp, FollPrimary.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: Etiqueta y Fecha
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Surface(color = FollYellow, shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = FollDarkBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Emergencia Real", fontSize = 12.sp, color = FollDarkBlue, fontWeight = FontWeight.Bold)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Hoy", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("14:32", fontSize = 14.sp, color = FollDarkBlue)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Elena Soto", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            Spacer(modifier = Modifier.height(16.dp))

            // Tiempo de Respuesta
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = FollDarkBlue, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = White, modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Tiempo de Respuesta", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                    Text("2 min 14 seg", fontSize = 14.sp, color = FollDarkBlue)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Observaciones
            Text("Observaciones", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Caída detectada en la cocina. El usuario no respondió al estímulo inicial. Los servicios de emergencia fueron contactados automáticamente.",
                fontSize = 14.sp, color = FollDarkBlue, lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Simulación del Mapa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(FollLightGreen.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Ubicación", tint = Color.Red, modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Ocultar Detalles
            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                border = BorderStroke(1.dp, FollDarkBlue),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Ocultar Detalles", color = FollDarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun FalsoPositivoCard(fecha: String, hora: String, nombre: String) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFBFFFFFF),
        border = BorderStroke(1.dp, FollPrimary.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Surface(color = FollPrimary.copy(alpha = 0.6f), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FollDarkBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Falso Positivo", fontSize = 12.sp, color = FollDarkBlue, fontWeight = FontWeight.Bold)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(fecha, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(hora, fontSize = 14.sp, color = FollDarkBlue)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                border = BorderStroke(1.dp, FollPrimary),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Ver Detalles", color = FollDarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}