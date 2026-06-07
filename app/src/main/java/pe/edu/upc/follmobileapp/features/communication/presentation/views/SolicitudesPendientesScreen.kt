package pe.edu.upc.follmobileapp.features.communication.presentation.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
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
import pe.edu.upc.follmobileapp.core.ui.components.FollTopBar
import pe.edu.upc.follmobileapp.core.ui.theme.*

@Composable
fun SolicitudesScreen(navController: NavController) {
    val backgroundGradient = Brush.linearGradient(colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2)))

    Scaffold(
        bottomBar = { FollBottomBar(navController, "care_screen") },
        topBar = { FollTopBar(navController, hasNotification = false, showBackButton = true) },
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
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = FollDarkBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Solicitudes Pendientes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFE0E0)) {
                        Text("2 Nuevas", color = Color(0xFFA00000), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SolicitudCard("Luis Silva", "Roberto Silva")
                Spacer(modifier = Modifier.height(16.dp))
                SolicitudCard("Ana Gómez", "Carmen Ruiz")
            }
        }
    }
}

@Composable
fun SolicitudCard(solicitante: String, abuelito: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFBFFFFFF),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("$solicitante solicita acceso para ver a ", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            Text("$abuelito.", fontSize = 16.sp, color = FollDarkBlue, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue)
                ) { Text("Aprobar") }
                
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f).height(40.dp),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) { Text("Rechazar", color = FollDarkGray) }
            }
        }
    }
}