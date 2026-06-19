package pe.edu.upc.follmobileapp.features.care.presentation.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.CaregiverRole
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.CuidadoresViewModel
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.CuidadoresViewModelFactory
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.CaregiverUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuidadoresScreen(
    navController: NavController,
    patientId: Long,
    viewModel: CuidadoresViewModel = viewModel(factory = CuidadoresViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Cargar cuidadores al iniciar
    LaunchedEffect(patientId) {
        viewModel.loadCaregivers(patientId)
    }

    // Escuchar mensajes de acción para mostrar Toasts
    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearActionMessage()
        }
    }

    var selectedCaregiverForDelete by remember { mutableStateOf<CaregiverUiModel?>(null) }
    var selectedCaregiverForToggle by remember { mutableStateOf<CaregiverUiModel?>(null) }
    var selectedContactForDelete by remember { mutableStateOf<pe.edu.upc.follmobileapp.features.care.domain.models.EmergencyContact?>(null) }
    var showAddContactDialog by remember { mutableStateOf(false) }

    val backgroundGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2), Color(0xFFFFFDF1), Color(0xFFFFFDF1), Color(0xFFFFFDF1))
    )

    Scaffold(
        bottomBar = { FollBottomBar(navController, "care_screen") },
        topBar = { FollTopBar(navController, showBackButton = true) }, // Habilitar retroceder
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
                Spacer(modifier = Modifier.height(24.dp))

                // Tarjeta Principal del Equipo con sombra premium
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFBFFFFFF),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            clip = false,
                            ambientColor = FollDarkBlue,
                            spotColor = FollDarkBlue
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.People, contentDescription = null, tint = FollDarkBlue, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Equipo de Cuidado",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FollDarkBlue
                                )
                                Text(
                                    text = "Abuelito: ${uiState.patientName}",
                                    fontSize = 14.sp,
                                    color = FollDarkGray
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        // Si soy Cuidador Principal o no
                        val isCurrentUserOfficial = uiState.currentUserRole == CaregiverRole.OFFICIAL_GUARDIAN

                        if (isCurrentUserOfficial) {
                            Text(
                                text = "Panel de Administración (Eres Cuidador Principal)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FollPrimary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        } else {
                            Text(
                                text = "Vista de lectura (Eres ${uiState.currentUserRole.label})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = FollDarkGray,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        Text(
                            text = "Cuidadores",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FollDarkBlue,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Listado de cuidadores
                        uiState.caregivers.forEach { caregiver ->
                            CuidadoresItemRow(
                                caregiver = caregiver,
                                isManageable = isCurrentUserOfficial && caregiver.role != CaregiverRole.OFFICIAL_GUARDIAN,
                                onDeleteClick = { selectedCaregiverForDelete = caregiver },
                                onToggleClick = { selectedCaregiverForToggle = caregiver }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Contactos de Emergencia",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = FollDarkBlue
                            )
                            if (isCurrentUserOfficial) {
                                TextButton(onClick = { showAddContactDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Agregar", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.emergencyContacts.isEmpty()) {
                            Text(
                                text = "No hay contactos de emergencia registrados.",
                                fontSize = 14.sp,
                                color = FollDarkGray,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            uiState.emergencyContacts.forEach { contact ->
                                EmergencyContactItemRow(
                                    contact = contact,
                                    isManageable = isCurrentUserOfficial,
                                    onDeleteClick = { selectedContactForDelete = contact }
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Modal de Confirmación de Eliminación Cuidador
    if (selectedCaregiverForDelete != null) {
        val caregiver = selectedCaregiverForDelete!!
        AlertDialog(
            onDismissRequest = { selectedCaregiverForDelete = null },
            title = { Text("¿Remover cuidador?", fontWeight = FontWeight.Bold, color = FollDarkBlue) },
            text = { Text("¿Deseas desvincular a ${caregiver.name} del equipo de cuidado de ${uiState.patientName}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeCaregiver(caregiver.id)
                        selectedCaregiverForDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FollError)
                ) {
                    Text("Remover", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCaregiverForDelete = null }) {
                    Text("Cancelar", color = FollDarkBlue)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    // Modal de Confirmación de Promoción/Toggle de Insignia Cuidador
    if (selectedCaregiverForToggle != null) {
        val caregiver = selectedCaregiverForToggle!!
        val isInvited = caregiver.role == CaregiverRole.INVITED_GUARDIAN
        val titleText = if (isInvited) "Quitar asignación de Principal Invitado" else "Asignar como Principal Invitado"
        val bodyText = if (isInvited) {
            "¿Deseas revocar la insignia de Principal Invitado a ${caregiver.name}? Volverá a ser un cuidador secundario."
        } else {
            "¿Deseas asignar a ${caregiver.name} como Principal Invitado? Esto le otorgará permisos especiales de visualización."
        }

        AlertDialog(
            onDismissRequest = { selectedCaregiverForToggle = null },
            title = { Text(titleText, fontWeight = FontWeight.Bold, color = FollDarkBlue) },
            text = { Text(bodyText) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.toggleCaregiverPromotion(caregiver.id)
                        selectedCaregiverForToggle = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue)
                ) {
                    Text("Confirmar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCaregiverForToggle = null }) {
                    Text("Cancelar", color = FollDarkBlue)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    // Modal de Confirmación de Eliminación de Contacto
    if (selectedContactForDelete != null) {
        val contact = selectedContactForDelete!!
        AlertDialog(
            onDismissRequest = { selectedContactForDelete = null },
            title = { Text("¿Eliminar contacto?", fontWeight = FontWeight.Bold, color = FollDarkBlue) },
            text = { Text("¿Deseas eliminar a ${contact.name} de la lista de contactos de emergencia?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEmergencyContact(contact.id)
                        selectedContactForDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FollError)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedContactForDelete = null }) {
                    Text("Cancelar", color = FollDarkBlue)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    // Dialog para Agregar Contacto de Emergencia
    if (showAddContactDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var relationship by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            title = { Text("Añadir Contacto", fontWeight = FontWeight.Bold, color = FollDarkBlue) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it.filter { c -> !c.isDigit() } },
                        label = { Text("Nombre Completo") },
                        placeholder = { Text("Ej. María González") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FollDarkBlue,
                            focusedLabelColor = FollDarkBlue
                        )
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it.filter { c -> c.isDigit() }.take(9) },
                        label = { Text("Teléfono") },
                        placeholder = { Text("Ej. 987654321") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FollDarkBlue,
                            focusedLabelColor = FollDarkBlue
                        )
                    )

                    OutlinedTextField(
                        value = relationship,
                        onValueChange = { relationship = it },
                        label = { Text("Parentesco / Relación") },
                        placeholder = { Text("Ej. Hijo, Vecino") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FollDarkBlue,
                            focusedLabelColor = FollDarkBlue
                        )
                    )

                    if (isError) {
                        Text(
                            text = "Por favor, completa todos los campos correctamente.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank() || phone.length < 9 || relationship.isBlank()) {
                            isError = true
                        } else {
                            viewModel.addEmergencyContact(name, phone, relationship)
                            showAddContactDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue)
                ) {
                    Text("Añadir", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) {
                    Text("Cancelar", color = FollDarkBlue)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun EmergencyContactItemRow(
    contact: pe.edu.upc.follmobileapp.features.care.domain.models.EmergencyContact,
    isManageable: Boolean,
    onDeleteClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFFDF1),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = contact.relationship,
                            fontSize = 12.sp,
                            color = FollDarkGray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = contact.phoneNumber,
                        fontSize = 14.sp,
                        color = FollDarkGray,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            if (isManageable) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar Contacto",
                        tint = FollError
                    )
                }
            }
        }
    }
}

@Composable
fun CuidadoresItemRow(
    caregiver: CaregiverUiModel,
    isManageable: Boolean,
    onDeleteClick: () -> Unit,
    onToggleClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFFDF1),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = caregiver.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = when (caregiver.role) {
                        CaregiverRole.OFFICIAL_GUARDIAN -> FollYellow
                        CaregiverRole.SECONDARY_GUARDIAN -> Color.LightGray.copy(alpha = 0.5f)
                        CaregiverRole.INVITED_GUARDIAN -> FollPaleYellow
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = caregiver.role.label,
                        fontSize = 12.sp,
                        color = when (caregiver.role) {
                            CaregiverRole.OFFICIAL_GUARDIAN -> FollOrange
                            CaregiverRole.INVITED_GUARDIAN -> FollDarkBlue
                            else -> FollDarkGray
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (isManageable) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón de insignia de seguridad / Shield
                    IconButton(onClick = onToggleClick) {
                        Icon(
                            imageVector = if (caregiver.role == CaregiverRole.INVITED_GUARDIAN) {
                                Icons.Default.Shield // Insignia rellena si está asignado
                            } else {
                                Icons.Outlined.Shield // Insignia vacía para asignar
                            },
                            contentDescription = "Insignia de Seguridad",
                            tint = FollDarkBlue
                        )
                    }

                    // Botón de eliminar
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remover Cuidador",
                            tint = FollError
                        )
                    }
                }
            }
        }
    }
}
