package pe.edu.upc.follmobileapp.features.iam.presentation.views

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.ui.components.FollBottomBar
import pe.edu.upc.follmobileapp.core.ui.components.FollTextField
import pe.edu.upc.follmobileapp.core.ui.components.FollTopBar
import pe.edu.upc.follmobileapp.core.ui.theme.*

@Composable
fun CrearAbuelitoScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var grupoSanguineo by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var enfermedades by remember { mutableStateOf("") }
    var medicamentos by remember { mutableStateOf("") }

    val backgroundGradient = Brush.linearGradient(colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2)))

    Scaffold(
        bottomBar = { FollBottomBar(navController, "care_screen") },
        topBar = { FollTopBar(navController) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Crear Abuelito", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    color = Color(0xFBFFFFFF),
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 32.dp)) {
                        FollTextField(label = "Nombre", placeholder = "Nombre", value = nombre, onValueChange = { nombre = it })
                        Spacer(modifier = Modifier.height(16.dp))
                        FollTextField(label = "Edad", placeholder = "Edad", value = edad, onValueChange = { edad = it })
                        Spacer(modifier = Modifier.height(16.dp))
                        FollTextField(label = "Grupo Sanguíneo", placeholder = "Grupo Sanguíneo", value = grupoSanguineo, onValueChange = { grupoSanguineo = it })
                        Spacer(modifier = Modifier.height(16.dp))
                        FollTextField(label = "DNI", placeholder = "DNI", value = dni, onValueChange = { dni = it })
                        Spacer(modifier = Modifier.height(16.dp))
                        FollTextField(label = "Enfermedades", placeholder = "Enfermedades", value = enfermedades, onValueChange = { enfermedades = it })
                        Spacer(modifier = Modifier.height(16.dp))
                        FollTextField(label = "Medicamentos", placeholder = "Medicamentos", value = medicamentos, onValueChange = { medicamentos = it })

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                            shape = RoundedCornerShape(30.dp)
                        ) { Text("Crear Abuelito", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}