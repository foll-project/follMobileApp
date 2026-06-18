package pe.edu.upc.follmobileapp.features.care.presentation.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import pe.edu.upc.follmobileapp.core.ui.components.FollTextField
import pe.edu.upc.follmobileapp.core.ui.components.FollTopBar
import pe.edu.upc.follmobileapp.core.ui.theme.*
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.AbuelitoDetailViewModel
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.AbuelitoDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbuelitoDetailScreen(
    navController: NavController,
    patientId: Long,
    viewModel: AbuelitoDetailViewModel = viewModel(factory = AbuelitoDetailViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLinkDeviceDialog by remember { mutableStateOf(false) }
    var showUnlinkDeviceDialog by remember { mutableStateOf(false) }
    var deviceIdInput by remember { mutableStateOf("") }

    // Cargar datos del abuelito al iniciar
    LaunchedEffect(patientId) {
        viewModel.loadPatient(patientId)
    }

    // Efectos de navegación tras guardar o eliminar
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            Toast.makeText(context, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
            viewModel.resetSavedState()
        }
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            Toast.makeText(context, "Abuelito eliminado", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearActionMessage()
        }
    }

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

                // Contenedor principal con sombra de marca
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
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Título de la pantalla
                        Text(
                            text = if (uiState.isEditMode) "Editar Perfil" else "Perfil del Abuelito",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = FollDarkBlue
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        if (!uiState.isEditMode) {
                            // MODO LECTURA: No son inputs, se ve como texto limpio
                            ProfileFieldText(label = "Nombre Completo", value = uiState.nombre)
                            ProfileFieldText(label = "Edad", value = "${uiState.edad} años")
                            ProfileFieldText(label = "Grupo Sanguíneo", value = uiState.grupoSanguineo)
                            ProfileFieldText(label = "DNI", value = uiState.dni)
                            ProfileFieldText(label = "Enfermedades", value = uiState.enfermedades)
                            ProfileFieldText(
                                label = "Medicamentos",
                                value = uiState.medicamentos.ifBlank { "Ninguno registrado" }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // SECCIÓN DISPOSITIVO
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Dispositivo IoT (Sensor)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FollDarkBlue
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val device = uiState.device
                                if (device == null) {
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LinkOff,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Sin dispositivo vinculado",
                                                fontWeight = FontWeight.Medium,
                                                color = Color.Gray,
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Button(
                                                onClick = {
                                                    deviceIdInput = ""
                                                    showLinkDeviceDialog = true
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                                                shape = RoundedCornerShape(20.dp),
                                                modifier = Modifier.height(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Vincular Dispositivo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                } else {
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                        border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.DeveloperBoard,
                                                        contentDescription = null,
                                                        tint = Color(0xFF2E7D32),
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Sensor: ${device.id}",
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF2E7D32),
                                                        fontSize = 15.sp
                                                    )
                                                }
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = if (device.status.equals("Online", ignoreCase = true)) Color(0xFF81C784) else Color(0xFFE0E0E0),
                                                    modifier = Modifier.padding(start = 8.dp)
                                                ) {
                                                    Text(
                                                        text = device.status,
                                                        color = Color.White,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = if (device.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                                                        contentDescription = null,
                                                        tint = if (device.batteryPercentage < 20) FollError else Color(0xFF2E7D32),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "${device.batteryPercentage}%" + if (device.isCharging) " (Cargando)" else "",
                                                        fontSize = 14.sp,
                                                        color = FollDarkGray
                                                    )
                                                }
                                                Text(
                                                    text = device.ultimoReporte,
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(12.dp))
                                            OutlinedButton(
                                                onClick = { showUnlinkDeviceDialog = true },
                                                border = BorderStroke(1.dp, FollError),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FollError),
                                                shape = RoundedCornerShape(20.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.LinkOff,
                                                    contentDescription = null,
                                                    tint = FollError,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Desvincular Dispositivo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Botón para ingresar a Modo Edición
                            Button(
                                onClick = { viewModel.toggleEditMode() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Editar Perfil", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                        } else {
                            // MODO EDICIÓN: Inputs interactivos y con validación
                            FollTextField(
                                label = "Nombre Completo",
                                placeholder = "Nombre",
                                value = uiState.nombre,
                                onValueChange = { viewModel.onNombreChange(it) },
                                isError = uiState.nombreError != null,
                                errorMessage = uiState.nombreError
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    FollTextField(
                                        label = "Edad",
                                        placeholder = "Edad",
                                        value = uiState.edad,
                                        onValueChange = { viewModel.onEdadChange(it) },
                                        isError = uiState.edadError != null,
                                        errorMessage = uiState.edadError
                                    )
                                }
                                
                                Box(modifier = Modifier.weight(1f)) {
                                    Column {
                                        Text(
                                            text = "Grupo Sanguíneo",
                                            color = if (uiState.grupoSanguineoError != null) MaterialTheme.colorScheme.error else FollDarkBlue,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                                            fontSize = 14.sp
                                        )
                                        
                                        var expanded by remember { mutableStateOf(false) }
                                        val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
                                        
                                        ExposedDropdownMenuBox(
                                            expanded = expanded,
                                            onExpandedChange = { expanded = !expanded }
                                        ) {
                                            OutlinedTextField(
                                                value = uiState.grupoSanguineo,
                                                onValueChange = {},
                                                readOnly = true,
                                                placeholder = { Text("Tipo", color = Color.Gray.copy(alpha = 0.5f)) },
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                                isError = uiState.grupoSanguineoError != null,
                                                shape = RoundedCornerShape(50.dp),
                                                modifier = Modifier
                                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                                    .fillMaxWidth(),
                                                colors = TextFieldDefaults.colors(
                                                    focusedContainerColor = Color.Transparent,
                                                    unfocusedContainerColor = Color.Transparent,
                                                    focusedIndicatorColor = FollDarkBlue,
                                                    unfocusedIndicatorColor = Color.LightGray,
                                                    errorContainerColor = Color.Transparent,
                                                    errorIndicatorColor = MaterialTheme.colorScheme.error
                                                )
                                            )
                                            ExposedDropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false },
                                                modifier = Modifier.background(Color.White)
                                            ) {
                                                bloodGroups.forEach { group ->
                                                    DropdownMenuItem(
                                                        text = { Text(text = group, color = FollDarkBlue, fontWeight = FontWeight.Medium) },
                                                        onClick = {
                                                            viewModel.onGrupoSanguineoChange(group)
                                                            expanded = false
                                                        },
                                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            FollTextField(
                                label = "DNI",
                                placeholder = "DNI",
                                value = uiState.dni,
                                onValueChange = { viewModel.onDniChange(it) },
                                isError = uiState.dniError != null,
                                errorMessage = uiState.dniError
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            FollTextField(
                                label = "Enfermedades",
                                placeholder = "Enfermedades",
                                value = uiState.enfermedades,
                                onValueChange = { viewModel.onEnfermedadesChange(it) },
                                isError = uiState.enfermedadesError != null,
                                errorMessage = uiState.enfermedadesError
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            FollTextField(
                                label = "Medicamentos",
                                placeholder = "Medicamentos (opcional)",
                                value = uiState.medicamentos,
                                onValueChange = { viewModel.onMedicamentosChange(it) }
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Botón Guardar Cambios
                            Button(
                                onClick = { viewModel.savePatient() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Botón Eliminar Abuelito (en color de error/alerta)
                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                border = BorderStroke(1.dp, FollError),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FollError),
                                shape = RoundedCornerShape(25.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = FollError)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Eliminar Abuelito", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Enlace para cancelar edición
                            TextButton(onClick = { viewModel.cancelEdit() }) {
                                Text("Cancelar", color = FollDarkGray, fontSize = 14.sp)
                            }
                        }
                    }
                }



                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Modal de Confirmación de Eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar abuelito?", fontWeight = FontWeight.Bold, color = FollDarkBlue) },
            text = { Text("Esta acción es irreversible y desvinculará al abuelito de todos sus cuidadores. ¿Deseas continuar?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deletePatient()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FollError)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = FollDarkBlue)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    // Modal de Vinculación de Dispositivo
    if (showLinkDeviceDialog) {
        AlertDialog(
            onDismissRequest = { showLinkDeviceDialog = false },
            title = { Text("Vincular Dispositivo", fontWeight = FontWeight.Bold, color = FollDarkBlue) },
            text = {
                Column {
                    Text("Ingresa el ID del dispositivo hardware para asociarlo al abuelito:", modifier = Modifier.padding(bottom = 12.dp))
                    OutlinedTextField(
                        value = deviceIdInput,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) {
                                deviceIdInput = input
                            }
                        },
                        placeholder = { Text("Ej: 1001") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = FollDarkBlue,
                            unfocusedIndicatorColor = Color.LightGray
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idVal = deviceIdInput.toIntOrNull()
                        if (idVal != null) {
                            showLinkDeviceDialog = false
                            viewModel.linkDevice(idVal)
                        } else {
                            Toast.makeText(context, "Por favor ingresa un ID válido", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Vincular", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLinkDeviceDialog = false }) {
                    Text("Cancelar", color = FollDarkBlue)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    // Modal de Confirmación de Desvinculación de Dispositivo
    if (showUnlinkDeviceDialog) {
        AlertDialog(
            onDismissRequest = { showUnlinkDeviceDialog = false },
            title = { Text("¿Desvincular dispositivo?", fontWeight = FontWeight.Bold, color = FollDarkBlue) },
            text = { Text("Se desasociará el sensor de este paciente y dejará de recibir alertas y telemetría en tiempo real. ¿Deseas continuar?") },
            confirmButton = {
                Button(
                    onClick = {
                        showUnlinkDeviceDialog = false
                        uiState.device?.id?.replace("#", "")?.trim()?.toIntOrNull()?.let { deviceId ->
                            viewModel.unlinkDevice(deviceId)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FollError)
                ) {
                    Text("Desvincular", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnlinkDeviceDialog = false }) {
                    Text("Cancelar", color = FollDarkBlue)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun ProfileFieldText(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = FollDarkBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
    }
}


