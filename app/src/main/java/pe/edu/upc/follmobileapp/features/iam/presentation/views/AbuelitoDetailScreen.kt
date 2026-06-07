package pe.edu.upc.follmobileapp.features.iam.presentation.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import pe.edu.upc.follmobileapp.core.ui.components.FollBottomBar
import pe.edu.upc.follmobileapp.core.ui.components.FollTextField
import pe.edu.upc.follmobileapp.core.ui.components.FollTopBar
import pe.edu.upc.follmobileapp.core.ui.theme.*

@Composable
fun AbuelitoDetailScreen(navController: NavController) {
    val backgroundGradient = Brush.linearGradient(colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2)))

    Scaffold(
        bottomBar = { FollBottomBar(navController, "care_screen") },
        topBar = { FollTopBar(navController, showBackButton = true) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
            Surface(
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                color = Color(0xFBFFFFFF),
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp).verticalScroll(rememberScrollState())
                ) {
                    // Ícono QR
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.QrCode, contentDescription = "QR", tint = FollDarkBlue)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Datos Médicos
                    FollTextField(label = "Nombre Completo", placeholder = "", value = "Carmen Rosa", onValueChange = {})
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) { FollTextField(label = "Edad", placeholder = "", value = "85", onValueChange = {}) }
                        Box(modifier = Modifier.weight(1f)) { FollTextField(label = "Grupo Sanguíneo", placeholder = "", value = "O+", onValueChange = {}) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(label = "DNI", placeholder = "", value = "17708023", onValueChange = {})
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(label = "Enfermedades", placeholder = "", value = "Hipertensión", onValueChange = {})
                    Spacer(modifier = Modifier.height(16.dp))
                    FollTextField(label = "Medicamentos", placeholder = "", value = "Losartán 50mg", onValueChange = {})

                    Spacer(modifier = Modifier.height(32.dp))

                    // Equipo de Cuidado
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, contentDescription = null, tint = FollDarkBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Equipo de cuidado", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    MiembroEquipoCard("María Gonzales", "Cuidador Principal", true)
                    Spacer(modifier = Modifier.height(8.dp))
                    MiembroEquipoCard("Jorge Silva", "Cuidador Secundario", false)

                    Spacer(modifier = Modifier.height(32.dp))

                    // Acciones
                    Text("Acciones", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { }, modifier = Modifier.fillMaxWidth().height(50.dp),
                        border = BorderStroke(1.dp, FollDarkBlue), shape = RoundedCornerShape(12.dp)
                    ) { Text("Ver Dispositivo", color = FollDarkBlue, fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { }, modifier = Modifier.fillMaxWidth().height(50.dp),
                        border = BorderStroke(1.dp, FollDarkBlue), shape = RoundedCornerShape(12.dp)
                    ) { Text("Anotaciones", color = FollDarkBlue, fontWeight = FontWeight.Bold) }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun MiembroEquipoCard(nombre: String, rol: String, esPrincipal: Boolean) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color(0x55CAEFE2), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(color = if (esPrincipal) FollYellow else Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp)) {
                    Text(rol, fontSize = 12.sp, color = if(esPrincipal) FollOrange else FollDarkGray, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }
            if (!esPrincipal) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Shield, contentDescription = "Permisos", tint = Color.Gray)
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
                }
            }
        }
    }
}