package pe.edu.upc.follmobileapp.features.emergency.presentation.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.navigation.Routes
import androidx.compose.ui.platform.LocalContext
import pe.edu.upc.follmobileapp.core.ui.components.FollBottomBar
import pe.edu.upc.follmobileapp.core.ui.components.FollTopBar
import pe.edu.upc.follmobileapp.core.ui.theme.*
import pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels.DashboardPatientState
import pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels.DashboardViewModel
import pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels.DashboardViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { FollTopBar(navController) },
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
            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta de Saludo Dinámica con Sombras Premium
            val hasEmergency = uiState.activeEmergenciesCount > 0
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (hasEmergency) 10.dp else 6.dp,
                        shape = RoundedCornerShape(32.dp),
                        clip = false,
                        ambientColor = if (hasEmergency) Color(0xFFEF5350) else FollDarkBlue.copy(alpha = 0.25f),
                        spotColor = if (hasEmergency) Color(0xFFEF5350) else FollDarkBlue
                    )
                    .background(
                        brush = Brush.linearGradient(
                            if (hasEmergency) {
                                listOf(White, Color(0xFFFFEBEE))
                            } else {
                                listOf(White, FollLightGreen)
                            }
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .border(
                        width = if (hasEmergency) 1.5.dp else 0.dp,
                        color = if (hasEmergency) Color(0xFFEF5350) else Color.Transparent,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Hola, ", fontSize = 24.sp, fontWeight = FontWeight.Normal, color = Color.Black)
                        Text(uiState.caregiverName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    if (hasEmergency) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alerta",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val emergencyNames = uiState.patients.filter { it.isInEmergency }.joinToString(", ") { it.name }
                            Text(
                                text = "¡Alerta activa! $emergencyNames requiere atención.",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    } else {
                        Text(
                            text = "Todo está tranquilo. ${uiState.patients.size} de tus familiares están bien.",
                            fontSize = 16.sp,
                            color = FollDarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sección: Acceso Directo
            Text("Acceso Directo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FollDarkGray)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatars de Abuelitos dinámicos
                uiState.patients.forEach { patient ->
                    AbuelitoAvatar(
                        patient = patient,
                        onClick = { navController.navigate("abuelito_detail_screen/${patient.id}") }
                    )
                }

                // Botón Añadir dinámico
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { navController.navigate(Routes.CrearAbuelito.route) }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = CircleShape,
                                clip = false,
                                ambientColor = FollDarkBlue.copy(alpha = 0.2f),
                                spotColor = FollDarkBlue
                            )
                            .border(2.dp, FollDarkBlue, CircleShape)
                            .background(White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir",
                            tint = FollDarkBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Añadir",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FollDarkBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sección: Estadísticas
            Text("Estadísticas de Seguridad", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FollDarkGray)
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(32.dp),
                color = White,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(32.dp),
                        clip = false,
                        ambientColor = FollDarkBlue.copy(alpha = 0.15f),
                        spotColor = FollDarkBlue
                    )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Gráfico de Dona Dinámico
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 30.dp.toPx()
                            val total = uiState.totalEvents
                            
                            if (total > 0) {
                                val falsePositivesAngle = (uiState.totalFalsePositives.toFloat() / total) * 360f
                                val realFallsAngle = (uiState.totalRealFalls.toFloat() / total) * 360f

                                // Falsos positivos (Amarillo)
                                drawArc(
                                    color = FollPaleYellow,
                                    startAngle = -90f,
                                    sweepAngle = falsePositivesAngle,
                                    useCenter = false,
                                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                                )
                                // Caídas Reales (Naranja)
                                drawArc(
                                    color = FollOrange,
                                    startAngle = -90f + falsePositivesAngle,
                                    sweepAngle = realFallsAngle,
                                    useCenter = false,
                                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                                )
                            } else {
                                // Círculo gris vacío si no hay eventos
                                drawArc(
                                    color = Color.LightGray.copy(alpha = 0.3f),
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(uiState.totalEvents.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("Eventos\nTotales", fontSize = 12.sp, color = FollDarkGray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Leyendas Estilizadas con Contenedores
                    LeyendaStat(color = FollPaleYellow, titulo = "Falsos Positivos", valor = uiState.totalFalsePositives.toString())
                    Spacer(modifier = Modifier.height(12.dp))
                    LeyendaStat(color = FollOrange, titulo = "Caídas Reales", valor = uiState.totalRealFalls.toString())

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón para ver Historial completo
                    Button(
                        onClick = { navController.navigate(Routes.History.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Ver Historial Completo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AbuelitoAvatar(
    patient: DashboardPatientState,
    onClick: () -> Unit
) {
    val initials = patient.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2).uppercase()
    val displayName = patient.name.substringBefore(" ")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Círculo del Avatar
            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(
                    width = if (patient.isInEmergency) 3.dp else 2.dp,
                    color = if (patient.isInEmergency) Color(0xFFEF5350) else FollPrimary.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .size(72.dp)
                    .shadow(
                        elevation = if (patient.isInEmergency) 12.dp else 4.dp,
                        shape = CircleShape,
                        clip = false,
                        ambientColor = if (patient.isInEmergency) Color(0xFFEF5350) else FollDarkBlue.copy(alpha = 0.2f),
                        spotColor = if (patient.isInEmergency) Color(0xFFEF5350) else FollDarkBlue
                    )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                if (patient.isInEmergency) {
                                    listOf(Color(0xFFEF5350), Color(0xFFC62828))
                                } else {
                                    listOf(FollDarkBlue, FollPrimary)
                                }
                            )
                        )
                ) {
                    Text(
                        text = initials,
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Indicador de Emergencia flotante
            if (patient.isInEmergency) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEF5350),
                    border = BorderStroke(1.5.dp, Color.White),
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "!",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = displayName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = FollDarkBlue
        )
    }
}

@Composable
fun LeyendaStat(color: Color, titulo: String, valor: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFBFBFB),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = color,
                    border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f)),
                    modifier = Modifier.size(16.dp)
                ) {}
                Spacer(modifier = Modifier.width(16.dp))
                Text(titulo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Text(valor, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
        }
    }
}