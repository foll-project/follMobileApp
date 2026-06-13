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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbuelitoDetailScreen(
    navController: NavController,
    patientId: Long,
    viewModel: AbuelitoDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

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


