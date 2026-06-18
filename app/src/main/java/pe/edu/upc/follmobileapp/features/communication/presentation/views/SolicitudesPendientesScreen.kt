package pe.edu.upc.follmobileapp.features.communication.presentation.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.ui.components.FollBottomBar
import pe.edu.upc.follmobileapp.core.ui.components.FollTopBar
import pe.edu.upc.follmobileapp.core.ui.theme.*
import pe.edu.upc.follmobileapp.features.communication.domain.models.CareRequest
import pe.edu.upc.follmobileapp.features.communication.presentation.viewmodels.SolicitudesViewModel
import pe.edu.upc.follmobileapp.features.communication.presentation.viewmodels.SolicitudesViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesScreen(
    navController: NavController,
    viewModel: SolicitudesViewModel = viewModel(factory = SolicitudesViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Escuchar mensajes de acción para Toasts
    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearActionMessage()
        }
    }

    val backgroundGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2), Color(0xFFFFFDF1), Color(0xFFFFFDF1))
    )

    val relationshipOptions = listOf(
        1 to "Hijo(a)",
        2 to "Vecino(a)",
        3 to "Enfermero(a)",
        4 to "Familiar"
    )

    var relationshipMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { FollBottomBar(navController, "care_screen") },
        topBar = { FollTopBar(navController, showBackButton = true) },
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
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // TÍTULO DE LA PANTALLA
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Mail, contentDescription = null, tint = FollDarkBlue, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Invitaciones",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = FollDarkBlue
                        )
                        Text(
                            text = "Gestiona el acceso compartido para cuidar a tus abuelitos",
                            fontSize = 13.sp,
                            color = FollDarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // CARD: ENVIAR NUEVA SOLICITUD
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFBFFFFFF),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(20.dp),
                            clip = false,
                            ambientColor = FollDarkBlue,
                            spotColor = FollDarkBlue
                        )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Solicitar acceso a un abuelito",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FollDarkBlue
                        )
                        Text(
                            text = "Ingresa el DNI del abuelito. Su cuidador principal aprobará la solicitud.",
                            fontSize = 13.sp,
                            color = FollDarkGray,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo DNI (8 dígitos)
                        OutlinedTextField(
                            value = uiState.dniInput,
                            onValueChange = { input ->
                                val filtered = input.filter { it.isDigit() }
                                if (filtered.length <= 8) {
                                    viewModel.onDniChanged(filtered)
                                }
                            },
                            label = { Text("DNI del abuelito") },
                            placeholder = { Text("Ej. 70123456") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FollDarkBlue,
                                focusedLabelColor = FollDarkBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Selector de Parentesco (Dropdown Menu)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            val currentOptionName = relationshipOptions.find { it.first == uiState.relationshipTypeId }?.second ?: "Familiar"
                            OutlinedTextField(
                                value = currentOptionName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Relación / Parentesco") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        modifier = Modifier.clickable { relationshipMenuExpanded = true }
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { relationshipMenuExpanded = true },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FollDarkBlue,
                                    focusedLabelColor = FollDarkBlue
                                )
                            )

                            DropdownMenu(
                                expanded = relationshipMenuExpanded,
                                onDismissRequest = { relationshipMenuExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .background(Color.White)
                            ) {
                                relationshipOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.second, color = Color.Black) },
                                        onClick = {
                                            viewModel.onRelationshipChanged(option.first)
                                            relationshipMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Botón de Envío
                        Button(
                            onClick = { viewModel.sendInvitation() },
                            enabled = !uiState.isFormLoading && uiState.dniInput.length == 8,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FollDarkBlue,
                                disabledContainerColor = Color.LightGray.copy(alpha = 0.5f)
                            )
                        ) {
                            if (uiState.isFormLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Enviar solicitud", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // TABS SELECTOR (Recibidas / Enviadas)
                TabRow(
                    selectedTabIndex = if (uiState.currentTab == "received") 0 else 1,
                    containerColor = Color.Transparent,
                    contentColor = FollDarkBlue,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[if (uiState.currentTab == "received") 0 else 1]),
                            color = FollDarkBlue
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = uiState.currentTab == "received",
                        onClick = { viewModel.onTabChanged("received") },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Recibidas", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                if (uiState.receivedPending.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = FollError,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    ) {
                                        Text(
                                            text = uiState.receivedPending.size.toString(),
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    )
                    Tab(
                        selected = uiState.currentTab == "sent",
                        onClick = { viewModel.onTabChanged("sent") },
                        text = { Text("Enviadas", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // CONTENIDO DE LOS TABS
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = FollDarkBlue)
                    }
                } else {
                    if (uiState.currentTab == "received") {
                        ReceivedSection(
                            pendingList = uiState.receivedPending,
                            historyList = uiState.receivedHistory,
                            onAccept = { viewModel.acceptInvitation(it) },
                            onReject = { viewModel.rejectInvitation(it) }
                        )
                    } else {
                        SentSection(sentList = uiState.sentInvitations)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ReceivedSection(
    pendingList: List<CareRequest>,
    historyList: List<CareRequest>,
    onAccept: (Long) -> Unit,
    onReject: (Long) -> Unit
) {
    if (pendingList.isEmpty() && historyList.isEmpty()) {
        EmptyInvitationsState(
            title = "No tienes invitaciones recibidas",
            subtitle = "Cuando un familiar solicite acceso a un abuelito donde eres cuidador principal, aparecerá aquí."
        )
    } else {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // PENDIENTES
            if (pendingList.isNotEmpty()) {
                Text(
                    text = "Pendientes de Aprobación",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FollDarkBlue,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                pendingList.forEach { invitation ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFFBC02D).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Iniciales del solicitante
                                InitialsAvatar(name = invitation.requesterName)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = invitation.requesterName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "Solicita acceso para cuidar a ${invitation.patientName}",
                                        fontSize = 13.sp,
                                        color = FollDarkGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Parentesco: ${invitation.relationshipName} · DNI Abuelito: ${invitation.patientDni}",
                                fontSize = 12.sp,
                                color = FollDarkGray,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(start = 48.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onReject(invitation.invitationId) },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, FollError.copy(alpha = 0.5f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FollError),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                ) {
                                    Text("Rechazar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                Button(
                                    onClick = { onAccept(invitation.invitationId) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FollLightGreen, contentColor = FollDarkBlue),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                ) {
                                    Text("Aprobar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // HISTORIAL RESUELTO
            if (historyList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Historial",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FollDarkGray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                historyList.forEach { invitation ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InitialsAvatar(name = invitation.requesterName, backgroundColor = Color.LightGray.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = invitation.requesterName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "${invitation.patientName} · ${invitation.relationshipName}",
                                    fontSize = 12.sp,
                                    color = FollDarkGray
                                )
                            }
                            StatusBadge(status = invitation.status)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SentSection(sentList: List<CareRequest>) {
    if (sentList.isEmpty()) {
        EmptyInvitationsState(
            title = "No has enviado invitaciones",
            subtitle = "Usa el formulario de arriba para enviar una solicitud de acceso a un abuelito."
        )
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Solicitudes Enviadas",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = FollDarkBlue,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            sentList.forEach { invitation ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InitialsAvatar(name = invitation.patientName, backgroundColor = FollLightGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = invitation.patientName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "DNI: ${invitation.patientDni} · Relación: ${invitation.relationshipName}",
                                fontSize = 12.sp,
                                color = FollDarkGray
                            )
                        }
                        StatusBadge(status = invitation.status)
                    }
                }
            }
        }
    }
}

@Composable
fun InitialsAvatar(name: String, backgroundColor: Color = FollYellow) {
    val initials = remember(name) {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }
        if (parts.isEmpty()) "?"
        else if (parts.size == 1) parts[0].take(2).uppercase()
        else (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        modifier = Modifier.size(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = initials,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = FollDarkBlue
            )
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (text, containerColor, textColor) = when (status) {
        "Accepted" -> Triple("Aceptada", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "Rejected" -> Triple("Rechazada", Color(0xFFFFEBEE), Color(0xFFC62828))
        else -> Triple("Pendiente", Color(0xFFFFF8E1), Color(0xFFF57F17))
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyInvitationsState(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = null,
                    tint = FollDarkBlue.copy(alpha = 0.5f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = FollDarkBlue
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = FollDarkGray,
            modifier = Modifier.padding(horizontal = 32.dp),
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}