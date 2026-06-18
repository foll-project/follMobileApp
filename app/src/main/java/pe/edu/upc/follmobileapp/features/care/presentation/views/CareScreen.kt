package pe.edu.upc.follmobileapp.features.care.presentation.views

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.navigation.Routes
import pe.edu.upc.follmobileapp.core.ui.components.FollBottomBar
import pe.edu.upc.follmobileapp.core.ui.components.FollTopBar
import pe.edu.upc.follmobileapp.core.ui.theme.*
import pe.edu.upc.follmobileapp.features.care.presentation.components.PatientCardItem
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.CareViewModel
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.CareViewModelFactory
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.CaregiverRole
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.PatientUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareScreen(
    navController: NavController,
    viewModel: CareViewModel = viewModel(factory = CareViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2), Color(0xFFFFFDF1), Color(0xFFFFFDF1), Color(0xFFFFFDF1))
    )

    Scaffold(
        bottomBar = { FollBottomBar(navController, "care_screen") },
        topBar = { FollTopBar(navController, hasNotification = true) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
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

                // Botones de acción principales
                Button(
                    onClick = { navController.navigate(Routes.CrearAbuelito.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Crear Abuelito", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { /* Lógica QR */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    border = BorderStroke(1.dp, Color.Transparent),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xBBFFFFFF))
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = FollDarkBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Vincular QR", color = FollDarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Lista de Tarjetas de Abuelitos desde el ViewModel
                uiState.patients.forEach { patient ->
                    val isExpanded = uiState.expandedPatientIds.contains(patient.id)
                    PatientCardItem(
                        patient = patient,
                        isExpanded = isExpanded,
                        onToggleExpand = { viewModel.togglePatientExpansion(patient.id) },
                        onVerPerfil = { navController.navigate("abuelito_detail_screen/${patient.id}") },
                        onVerCuidadores = { navController.navigate("cuidadores_screen/${patient.id}") },
                        onVerAnotaciones = { navController.navigate("anotaciones_screen/${patient.id}") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
