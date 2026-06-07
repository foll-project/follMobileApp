package pe.edu.upc.follmobileapp.features.emergency.presentation.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.ui.components.FollBottomBar
import pe.edu.upc.follmobileapp.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        bottomBar = { FollBottomBar(navController, "dashboard_screen") },
        containerColor = FollBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header: Logo Foll
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = FollDarkBlue, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = White, modifier = Modifier.padding(4.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Foll", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta de Saludo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(Brush.linearGradient(listOf(White, FollLightGreen)))
                    .padding(24.dp)
            ) {
                Column {
                    Row {
                        Text("Hola, ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("María", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Todo está tranquilo. 3 de tus familiares están bien.", fontSize = 16.sp, color = FollDarkGray)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sección: Acceso Directo
            Text("Acceso Directo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FollDarkGray)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AbuelitoAvatar("Carmen")
                AbuelitoAvatar("Antonio")
                AbuelitoAvatar("Rosa")

                // Botón Añadir
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(70.dp)
                            .border(1.dp, FollDarkBlue, CircleShape)
                            .padding(2.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir", tint = FollDarkBlue, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Añadir", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sección: Estadísticas
            Text("Estadísticas de Seguridad", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FollDarkGray)
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(32.dp),
                color = White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Gráfico de Dona
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 30.dp.toPx()
                            // Falsos positivos (Amarillo)
                            drawArc(
                                color = FollPaleYellow,
                                startAngle = 120f, sweepAngle = 300f,
                                useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Round)
                            )
                            // Caídas Reales (Naranja)
                            drawArc(
                                color = FollOrange,
                                startAngle = 60f, sweepAngle = 60f,
                                useCenter = false, style = Stroke(strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("24", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("Eventos\nTotales", fontSize = 12.sp, color = FollDarkGray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Leyenda
                    LeyendaStat(color = FollPaleYellow, titulo = "Falsos Positivos", valor = "20")
                    Spacer(modifier = Modifier.height(16.dp))
                    LeyendaStat(color = FollOrange, titulo = "Caídas Reales", valor = "4")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AbuelitoAvatar(nombre: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = FollDarkBlue,
            modifier = Modifier.size(70.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(nombre, color = White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun LeyendaStat(color: Color, titulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = color, modifier = Modifier.size(16.dp)) {}
            Spacer(modifier = Modifier.width(16.dp))
            Text(titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        Text(valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
    }
}