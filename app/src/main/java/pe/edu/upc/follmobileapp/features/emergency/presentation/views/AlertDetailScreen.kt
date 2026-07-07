package pe.edu.upc.follmobileapp.features.emergency.presentation.views

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.ui.components.FollBottomBar
import pe.edu.upc.follmobileapp.core.ui.components.FollTopBar
import pe.edu.upc.follmobileapp.core.ui.theme.*
import pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels.AlertViewModel
import pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels.AlertViewModelFactory

private val CardShape = RoundedCornerShape(24.dp)
private val EmergencyAccent = Color(0xFFEF5350)
private val EmergencyTint = Color(0xFFFFEBEE)

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
    val context = LocalContext.current
    var showMedicalDialog by remember { mutableStateOf(false) }
    var showAttendDialog by remember { mutableStateOf(false) }

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

    // Mismo degradado Foll que el resto de la app, con un toque muy suave de urgencia al inicio.
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF6F8A7),
            Color(0xFFFFF0F0),
            Color(0xFFCAEFE2),
            Color(0xFFFFFDF1),
            Color(0xFFFFFDF1)
        )
    )

    Scaffold(
        topBar = { FollTopBar(navController, showBackButton = true) },
        bottomBar = { FollBottomBar(navController, "alerts_screen") },
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
                    Text(
                        text = "Alerta no encontrada",
                        fontSize = 18.sp,
                        color = FollDarkBlue
                    )
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
                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Tarjeta principal del paciente ──
                    Surface(
                        shape = CardShape,
                        color = Color(0xFBFFFFFF),
                        border = BorderStroke(1.5.dp, EmergencyAccent.copy(alpha = 0.55f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = CardShape,
                                clip = false,
                                ambientColor = FollDarkBlue,
                                spotColor = FollDarkBlue
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Encabezado de emergencia (mismo patrón que Anotaciones / Solicitudes)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = EmergencyTint,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = FollError,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Alerta de caída",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FollDarkBlue
                                    )
                                    Text(
                                        text = "Requiere atención inmediata",
                                        fontSize = 14.sp,
                                        color = FollDarkGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = alert.patientName,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = FollDarkBlue,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Pill: tiempo transcurrido (acento rojo puntual, como PatientCardItem)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EmergencyTint,
                                border = BorderStroke(1.dp, EmergencyAccent.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = FollError,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Hace ${alert.elapsedMinutes} min",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FollError
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Pill: tipo de caída (verde Foll, como el resto de la app)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = FollLightGreen.copy(alpha = 0.45f),
                                border = BorderStroke(1.dp, FollPrimary.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sensors,
                                        contentDescription = null,
                                        tint = FollDarkBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = alert.fallType,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = FollDarkBlue
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Tarjeta de ubicación ──
                    Surface(
                        shape = CardShape,
                        color = Color(0xFBFFFFFF),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = CardShape,
                                clip = false,
                                ambientColor = FollDarkBlue,
                                spotColor = FollDarkBlue
                            )
                            .clickable {
                                uriHandler.openUri(
                                    "https://www.google.com/maps/search/?api=1&query=${alert.latitude},${alert.longitude}"
                                )
                            }
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = FollDarkBlue,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Ubicación del evento",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FollDarkBlue
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = alert.address,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = FollDarkGray,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = FollLightGreen.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, FollPrimary.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = null,
                                        tint = FollDarkBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Abrir en Google Maps",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = FollDarkBlue
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Acciones (jerarquía Foll: 1 primario azul, 2 secundarios outline) ──

                    // 1. Atender — acción principal
                    Button(
                        onClick = { showAttendDialog = true },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = White,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Atender emergencia",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Llamar ambulancia — secundario urgente (outline rojo Foll)
                    OutlinedButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:105")))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        border = BorderStroke(1.5.dp, FollError),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FollError),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Llamar ambulancia",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Ficha médica — terciario (como "Vincular QR" en CareScreen)
                    OutlinedButton(
                        onClick = { showMedicalDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        border = BorderStroke(1.dp, Color.Transparent),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xBBFFFFFF),
                            contentColor = FollDarkBlue
                        ),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ver ficha médica y anotaciones",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Al atender, se cerrará la alerta y se avisará a los demás cuidadores.",
                        fontSize = 12.sp,
                        color = FollDarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // ── Diálogo: ficha médica ──
    if (showMedicalDialog && alert != null) {
        AlertDialog(
            onDismissRequest = { showMedicalDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = FollDarkBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Ficha médica",
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
                    MedicalField(label = "Nombre completo", value = alert.patientName)
                    Spacer(modifier = Modifier.height(12.dp))
                    MedicalField(label = "DNI", value = alert.dni)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MedicalField(
                            label = "Edad",
                            value = "${alert.age} años",
                            modifier = Modifier.weight(1f)
                        )
                        MedicalField(
                            label = "Grupo sanguíneo",
                            value = alert.bloodType,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    MedicalField(label = "Condiciones médicas", value = alert.medicalConditions)
                    Spacer(modifier = Modifier.height(12.dp))
                    MedicalField(label = "Medicamentos", value = alert.medications)

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.35f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Últimas anotaciones",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FollDarkBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val annotations = uiState.patientAnnotations[alert.patientId] ?: emptyList()
                    if (annotations.isEmpty()) {
                        Text(
                            text = "No hay anotaciones registradas.",
                            fontSize = 14.sp,
                            color = FollDarkGray
                        )
                    } else {
                        annotations.forEach { annotation ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = FollBackground,
                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "${annotation.dateString} · ${annotation.authorName}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FollDarkGray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = annotation.content,
                                        fontSize = 14.sp,
                                        color = FollDarkBlue,
                                        lineHeight = 20.sp
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
            shape = RoundedCornerShape(24.dp),
            containerColor = White
        )
    }

    // ── Diálogo: confirmar atención ──
    if (showAttendDialog && alert != null) {
        AlertDialog(
            onDismissRequest = { showAttendDialog = false },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = FollLightGreen.copy(alpha = 0.5f),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = FollDarkBlue,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "¿Atender esta emergencia?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FollDarkBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Confirmas que tú te haces cargo de la caída de ${alert.patientName}. La alerta se cerrará y se avisará en tiempo real a los demás cuidadores.",
                    fontSize = 15.sp,
                    color = FollDarkGray,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAttendDialog = false
                        viewModel.attendAlert(alert.patientId) {
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Sí, la atiendo", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAttendDialog = false }) {
                    Text("Cancelar", fontWeight = FontWeight.Bold, color = FollDarkGray)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = White
        )
    }
}

@Composable
private fun MedicalField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 12.sp, color = FollDarkGray)
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = FollDarkBlue
        )
    }
}
