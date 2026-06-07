package pe.edu.upc.follmobileapp.features.emergency.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.ui.theme.*

@Composable
fun AlertDetailScreen(navController: NavController) {
    val backgroundGradient = Brush.linearGradient(colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2)))
    val redAlertColor = Color(0xFFA04040)

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón Regresar
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = FollDarkBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Regresar", color = FollDarkBlue, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título de Alerta
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = redAlertColor, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.Warning, contentDescription = "Peligro", tint = Color.White, modifier = Modifier.padding(10.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("ALERTA", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = redAlertColor, letterSpacing = 2.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta: Nombre y Estado
            Surface(shape = RoundedCornerShape(32.dp), color = Color(0xFBFFFFFF), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Carlos Mendoza", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sensors, contentDescription = "Sensor", tint = redAlertColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Posible caída detectada\nhace 1 min", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = redAlertColor, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta: Mapa de Ubicación
            Surface(shape = RoundedCornerShape(32.dp), color = Color(0xFBFFFFFF), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(24.dp)).background(FollLightGreen.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Ubicación", tint = Color.Red, modifier = Modifier.size(56.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Haga click para ver la ubicación", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = FollDarkGray, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta: Información Crítica
            Surface(shape = RoundedCornerShape(32.dp), color = Color(0xFBFFFFFF), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MedicalServices, contentDescription = "Info Médica", tint = redAlertColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Información Crítica", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Condiciones", fontSize = 14.sp, color = FollDarkGray)
                            Text("Hipertensión\nArritmia", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Tipo de sangre", fontSize = 14.sp, color = FollDarkGray)
                            Text("O+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Medicamentos", fontSize = 14.sp, color = FollDarkGray)
                            Text("Amiodarona\nLosartán", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Edad", fontSize = 14.sp, color = FollDarkGray)
                            Text("85", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de Acción
            Button(
                onClick = { /* Lógica para llamar ambulancia */ },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                shape = RoundedCornerShape(30.dp)
            ) {
                Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("LLAMAR AMBULANCIA", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Lógica para ir en camino */ },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FollPaleYellow),
                shape = RoundedCornerShape(30.dp)
            ) {
                Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = FollDarkBlue)
                Spacer(modifier = Modifier.width(12.dp))
                Text("¡VOY EN CAMINO!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
            }
        }
    }
}