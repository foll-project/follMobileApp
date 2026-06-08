package pe.edu.upc.follmobileapp.features.iam.presentation.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.navigation.Routes
import pe.edu.upc.follmobileapp.core.ui.components.FollBottomBar
import pe.edu.upc.follmobileapp.core.ui.components.FollTopBar
import pe.edu.upc.follmobileapp.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareScreen(navController: NavController) {
    val backgroundGradient = Brush.linearGradient(colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2), Color(0xFFFDF1), Color(0xFFFDF1), Color(0xFFFDF1)))

    Scaffold(
        bottomBar = { FollBottomBar(navController, "care_screen") },
        topBar = { FollTopBar(navController, hasNotification = true) },
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
                Text("Mis Abuelitos", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Gestiona los perfiles y dispositivos de cuidado.", fontSize = 16.sp, color = FollDarkBlue)

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de acción
                Button(
                    onClick = { navController.navigate(Routes.CrearAbuelito.route) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Abuelito", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { /* Lógica QR */ },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    border = BorderStroke(1.dp, Color.Transparent),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xBBFFFFFF))
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = FollDarkBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Vincular QR", color = FollDarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Tarjetas de Abuelitos
                AbuelitoCard(
                    nombre = "Carmen Rosa",
                    rol = "Cuidador Principal",
                    esPrincipal = true,
                    onClickDetalles = { navController.navigate(Routes.AbuelitoDetail.route) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                AbuelitoCard(
                    nombre = "Don Roberto",
                    rol = "Cuidador Secundario",
                    esPrincipal = false,
                    onClickDetalles = { navController.navigate(Routes.AbuelitoDetail.route) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun AbuelitoCard(nombre: String, rol: String, esPrincipal: Boolean, onClickDetalles: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = Color(0xFBFFFFFF),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Surface(shape = CircleShape, color = FollPrimary, modifier = Modifier.size(12.dp)) {}
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                color = if (esPrincipal) FollYellow.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Icon(
                        imageVector = if (esPrincipal) Icons.Default.Star else Icons.Default.People,
                        contentDescription = null, 
                        tint = if (esPrincipal) FollOrange else FollDarkBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(rol, fontSize = 14.sp, color = FollDarkBlue, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onClickDetalles() }) {
                    Text("Ver Detalles", color = FollDarkBlue, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = FollDarkBlue, modifier = Modifier.size(16.dp))
                }
                Icon(Icons.Default.QrCode, contentDescription = "QR", tint = FollDarkBlue)
            }
        }
    }
}

