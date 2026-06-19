package pe.edu.upc.follmobileapp.features.emergency.presentation.views

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.ui.components.FollBottomBar
import pe.edu.upc.follmobileapp.core.ui.components.FollTopBar
import pe.edu.upc.follmobileapp.core.ui.theme.*
import pe.edu.upc.follmobileapp.features.emergency.domain.models.FallIncident
import pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels.FallAnnotation
import pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels.HistoryViewModel
import pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels.HistoryViewModelFactory

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = viewModel(factory = HistoryViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearActionMessage()
        }
    }

    val backgroundGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF6F8A7),
            Color(0xFFCAEFE2),
            Color(0xFFFFFDF1),
            Color(0xFFFFFDF1),
            Color(0xFFFFFDF1)
        )
    )

    Scaffold(
        bottomBar = { FollBottomBar(navController, "history_screen") },
        topBar = { FollTopBar(navController) },
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
                Text(
                    text = "Registro de Caídas",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = FollDarkBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Historial cronológico de eventos",
                    fontSize = 16.sp,
                    color = FollDarkBlue
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Selector de Abuelitos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.filterPatients.forEach { (patientId, label) ->
                        val isSelected = uiState.selectedPatientId == patientId
                        val containerColor = if (isSelected) FollDarkBlue else Color.White
                        val contentColor = if (isSelected) Color.White else FollDarkBlue
                        val borderStroke = if (isSelected) null else BorderStroke(1.dp, FollDarkBlue.copy(alpha = 0.3f))

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = containerColor,
                            contentColor = contentColor,
                            border = borderStroke,
                            modifier = Modifier
                                .shadow(
                                    elevation = if (isSelected) 4.dp else 1.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    clip = false,
                                    ambientColor = FollDarkBlue,
                                    spotColor = FollDarkBlue
                                )
                                .clickable { viewModel.selectPatientFilter(patientId) }
                        ) {
                            Text(
                                text = label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                if (uiState.filteredIncidents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay caídas registradas.",
                            fontSize = 16.sp,
                            color = FollDarkGray
                        )
                    }
                } else {
                    uiState.filteredIncidents.forEachIndexed { index, incident ->
                        val isLast = index == uiState.filteredIncidents.lastIndex
                        val isExpanded = uiState.expandedIncidentIds.contains(incident.id)
                        val isEditing = uiState.editingIncidentId == incident.id
                        val patientNotes = uiState.patientAnnotations[incident.patientId] ?: emptyList()

                        TimelineItem(isLast = isLast) {
                            if (incident.isRealEmergency) {
                                EmergenciaRealCard(
                                    incident = incident,
                                    isExpanded = isExpanded,
                                    onToggleExpand = { viewModel.toggleIncidentExpansion(incident.id) },
                                    isEditing = isEditing,
                                    editingText = uiState.editingObservationsText,
                                    onStartEdit = { viewModel.startEditing(incident.id, incident.observations) },
                                    onTextChanged = { viewModel.onObservationsChange(it) },
                                    onSaveEdit = { viewModel.saveObservations() },
                                    onCancelEdit = { viewModel.cancelEditing() },
                                    annotations = patientNotes
                                )
                            } else {
                                FalsoPositivoCard(
                                    incident = incident,
                                    isEditing = isEditing,
                                    editingText = uiState.editingObservationsText,
                                    onStartEdit = { viewModel.startEditing(incident.id, incident.observations) },
                                    onTextChanged = { viewModel.onObservationsChange(it) },
                                    onSaveEdit = { viewModel.saveObservations() },
                                    onCancelEdit = { viewModel.cancelEditing() }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun TimelineItem(isLast: Boolean, content: @Composable () -> Unit) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        // Línea y Punto de la línea de tiempo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            // Punto dorado
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(FollOrange, CircleShape)
            )
            // Línea vertical
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(FollOrange.copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Contenido de la tarjeta
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 24.dp)
        ) {
            content()
        }
    }
}

@Composable
fun EmergenciaRealCard(
    incident: FallIncident,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    isEditing: Boolean,
    editingText: String,
    onStartEdit: () -> Unit,
    onTextChanged: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    annotations: List<FallAnnotation>
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFBFFFFFF),
        border = BorderStroke(1.dp, FollPrimary.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false,
                ambientColor = FollDarkBlue,
                spotColor = FollDarkBlue
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: Etiqueta y Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(color = FollYellow, shape = RoundedCornerShape(12.dp)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = FollDarkBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Emergencia Real",
                            fontSize = 12.sp,
                            color = FollDarkBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = incident.dateString,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = incident.timeString,
                        fontSize = 14.sp,
                        color = FollDarkBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = incident.patientName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tipo de Caída
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = FollOrange.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = FollOrange,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Tipo de Caída",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = incident.fallType,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tiempo de Respuesta
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = FollDarkBlue.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = FollDarkBlue,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Tiempo de Respuesta",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = incident.responseTime,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Panel Detalle Expandido
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    HorizontalDivider(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Observaciones
                    Text(
                        text = "Observaciones",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (isEditing) {
                        OutlinedTextField(
                            value = editingText,
                            onValueChange = onTextChanged,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FollDarkBlue,
                                unfocusedBorderColor = Color.LightGray,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onSaveEdit,
                                colors = ButtonDefaults.buttonColors(containerColor = FollYellow),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Guardar",
                                    color = FollDarkBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            OutlinedButton(
                                onClick = onCancelEdit,
                                border = BorderStroke(1.dp, FollDarkBlue),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Cancelar", color = FollDarkBlue)
                            }
                        }
                    } else {
                        Text(
                            text = incident.observations.ifBlank { "Sin observaciones registradas." },
                            fontSize = 14.sp,
                            color = FollDarkBlue,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onStartEdit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            border = BorderStroke(1.dp, FollDarkBlue),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = FollDarkBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Editar Observaciones",
                                color = FollDarkBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Ubicación (Mapa Simulado)
                    Text(
                        text = "Ubicación",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(FollLightGreen.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Ubicación",
                                tint = Color.Red,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Coord: ${incident.latitude}, ${incident.longitude}",
                                fontSize = 11.sp,
                                color = FollDarkBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Anotaciones (5 últimas anotaciones registradas por ahora)
                    Text(
                        text = "Anotaciones de Cuidado (Últimas 5)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (annotations.isEmpty()) {
                        Text(
                            text = "No hay anotaciones registradas para este paciente.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    } else {
                        annotations.take(5).forEach { annotation ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFFFDF1),
                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${annotation.dateString} - ${annotation.authorName}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FollDarkGray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = annotation.content,
                                        fontSize = 13.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Ocultar / Ver Detalles
            OutlinedButton(
                onClick = onToggleExpand,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                border = BorderStroke(1.dp, FollDarkBlue),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = if (isExpanded) "Ocultar Detalles" else "Ver Detalles",
                    color = FollDarkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun FalsoPositivoCard(
    incident: FallIncident,
    isEditing: Boolean,
    editingText: String,
    onStartEdit: () -> Unit,
    onTextChanged: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFBFFFFFF),
        border = BorderStroke(1.dp, FollPrimary.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false,
                ambientColor = FollDarkBlue,
                spotColor = FollDarkBlue
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: Etiqueta y Fecha/Hora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    color = FollPrimary.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = FollDarkBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Falso Positivo",
                            fontSize = 12.sp,
                            color = FollDarkBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = incident.dateString,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = incident.timeString,
                        fontSize = 14.sp,
                        color = FollDarkBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = incident.patientName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Observaciones
            Text(
                text = "Observaciones",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = editingText,
                    onValueChange = onTextChanged,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FollDarkBlue,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSaveEdit,
                        colors = ButtonDefaults.buttonColors(containerColor = FollYellow),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Guardar",
                            color = FollDarkBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedButton(
                        onClick = onCancelEdit,
                        border = BorderStroke(1.dp, FollDarkBlue),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Cancelar", color = FollDarkBlue)
                    }
                }
            } else {
                Text(
                    text = incident.observations.ifBlank { "Sin observaciones registradas." },
                    fontSize = 14.sp,
                    color = FollDarkBlue,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onStartEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    border = BorderStroke(1.dp, FollDarkBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = FollDarkBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Editar Observaciones",
                        color = FollDarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}