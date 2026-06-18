package pe.edu.upc.follmobileapp.features.emergency.presentation.views

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.ui.theme.*
import pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels.AlertViewModel
import pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels.AlertViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailScreen(
    navController: NavController,
    alertId: Long,
    viewModel: AlertViewModel = viewModel(factory = AlertViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val alert = uiState.alerts.firstOrNull { it.id == alertId }
    val uriHandler = LocalUriHandler.current
    var showMedicalDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearActionMessage()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { err ->
            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
        }
    }

    // Gradiente premium suave, llamativo pero reconfortante y profesional (arena, coral pastel y blanco)
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFF8F8),
            Color(0xFFFFECEC),
            Color(0xFFFFFBF0),
            Color(0xFFFFF8F8)
        )
    )
    val redAlertColor = Color(0xFFC62828)

    Scaffold(
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            if (alert == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Alerta no encontrada", fontSize = 18.sp, color = FollDarkBlue)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Encabezado con botón de retroceso flotante y ALERTA
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(44.dp),
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                        ) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Retroceder",
                                    tint = redAlertColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "ALERTA",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = redAlertColor,
                            letterSpacing = 2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tarjeta Principal (Glassmorphism sutil y bordes cuidados)
                    Surface(
                        shape = RoundedCornerShape(32.dp),
                        color = Color(0xE6FFFFFF), // Blanco translúcido premium
                        border = BorderStroke(1.5.dp, Color(0xFFEF5350).copy(alpha = 0.25f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(32.dp),
                                clip = false,
                                ambientColor = redAlertColor,
                                spotColor = redAlertColor
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Nombre del paciente en letra prominente y elegante
                            Text(
                                text = alert.patientName,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = FollDarkBlue,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(18.dp))
                            
                            // Insignia de tiempo transcurrido (Pill)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFFFEBEE),
                                border = BorderStroke(1.dp, Color(0xFFEF5350).copy(alpha = 0.4f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Tiempo",
                                        tint = redAlertColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Hace ${alert.elapsedMinutes} minutos",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = redAlertColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Insignia de tipo de caída (Pill)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = FollLightGreen.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, FollDarkBlue.copy(alpha = 0.15f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sensors,
                                        contentDescription = "Sensor",
                                        tint = FollDarkBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = alert.fallType,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FollDarkBlue,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Tarjeta de Ubicación del Evento (Elegante GPS Card sin foto)
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = Color(0xE6FFFFFF),
                        border = BorderStroke(1.dp, FollDarkBlue.copy(alpha = 0.12f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(28.dp),
                                clip = false,
                                ambientColor = FollDarkBlue,
                                spotColor = FollDarkBlue
                            )
                            .clickable {
                                uriHandler.openUri("https://www.google.com/maps/search/?api=1&query=${alert.latitude},${alert.longitude}")
                            }
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = redAlertColor.copy(alpha = 0.12f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Ubicación",
                                        tint = redAlertColor,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Ubicación del Evento",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = alert.address,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = FollDarkBlue,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Indicador de Google Maps táctil
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFFEBEE),
                                border = BorderStroke(1.dp, redAlertColor.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = "Google Maps",
                                        tint = redAlertColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Tocar para abrir en Google Maps",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = redAlertColor
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón para acceder a la ficha médica de datos (Popup dialog)
                    Button(
                        onClick = { showMedicalDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(4.dp, RoundedCornerShape(28.dp), ambientColor = FollDarkBlue, spotColor = FollDarkBlue),
                        colors = ButtonDefaults.buttonColors(containerColor = FollYellow),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = FollDarkBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VER FICHA MÉDICA Y ANOTACIONES",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = FollDarkBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de Llamar Ambulancia (Color verde premium y dialer icon)
                    Button(
                        onClick = { /* Lógica de llamada externa */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(4.dp, RoundedCornerShape(28.dp), ambientColor = Color(0xFF2E7D32), spotColor = Color(0xFF2E7D32)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Llamar",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "LLAMAR AMBULANCIA",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón Voy en camino
                    Button(
                        onClick = {
                            viewModel.acknowledgeAlert(alert.id) {
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FollPaleYellow),
                        border = BorderStroke(1.dp, FollDarkBlue),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsRun,
                            contentDescription = null,
                            tint = FollDarkBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¡VOY EN CAMINO!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FollDarkBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Modal Popup con la Ficha Médica y Anotaciones (Modernizado)
    if (showMedicalDialog && alert != null) {
        AlertDialog(
            onDismissRequest = { showMedicalDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = FollOrange.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = FollOrange,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Ficha Médica",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = FollDarkBlue
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Nombre
                    Text("Nombre Completo", fontSize = 12.sp, color = FollDarkGray)
                    Text(alert.patientName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))

                    // DNI
                    Text("DNI", fontSize = 12.sp, color = FollDarkGray)
                    Text(alert.dni, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Edad y Sangre
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Edad", fontSize = 12.sp, color = FollDarkGray)
                            Text("${alert.age} años", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Grupo Sanguíneo", fontSize = 12.sp, color = FollDarkGray)
                            Text(alert.bloodType, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Condiciones
                    Text("Condiciones Médicas", fontSize = 12.sp, color = FollDarkGray)
                    Text(alert.medicalConditions, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Medicamentos
                    Text("Medicamentos Prescritos", fontSize = 12.sp, color = FollDarkGray)
                    Text(alert.medications, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(20.dp))

                    // Anotaciones
                    Text(
                        text = "Últimas Anotaciones de Cuidado",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = FollDarkBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val annotations = uiState.patientAnnotations[alert.patientId] ?: emptyList()
                    if (annotations.isEmpty()) {
                        Text("No hay anotaciones registradas.", fontSize = 14.sp, color = Color.Gray)
                    } else {
                        annotations.forEach { annotation ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFFFFDF1),
                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .shadow(2.dp, RoundedCornerShape(16.dp), ambientColor = FollDarkBlue, spotColor = FollDarkBlue)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "${annotation.dateString} - ${annotation.authorName}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FollDarkGray
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = annotation.content,
                                        fontSize = 14.sp,
                                        color = Color.Black,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showMedicalDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White
        )
    }
}