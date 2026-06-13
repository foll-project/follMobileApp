package pe.edu.upc.follmobileapp.features.care.presentation.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.CrearAbuelitoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearAbuelitoScreen(
    navController: NavController,
    viewModel: CrearAbuelitoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Efecto secundario al guardar con éxito
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            Toast.makeText(context, "Abuelito creado exitosamente", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    val backgroundGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFF6F8A7), Color(0xFFCAEFE2), Color(0xFFFFFDF1), Color(0xFFFFFDF1), Color(0xFFFFFDF1))
    )

    Scaffold(
        bottomBar = { FollBottomBar(navController, "care_screen") },
        topBar = { FollTopBar(navController, showBackButton = true) }, // Se habilitó el botón para retroceder
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

                // Contenedor del formulario con sombra premium de color de marca
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
                        // Título dentro del formulario
                        Text(
                            text = "Crear Abuelito",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = FollDarkBlue
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Campo Nombre (sin números)
                        FollTextField(
                            label = "Nombre",
                            placeholder = "Nombre completo",
                            value = uiState.nombre,
                            onValueChange = { viewModel.onNombreChange(it) },
                            isError = uiState.nombreError != null,
                            errorMessage = uiState.nombreError
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo Edad (filtrado numérico)
                        FollTextField(
                            label = "Edad",
                            placeholder = "Edad (solo números)",
                            value = uiState.edad,
                            onValueChange = { viewModel.onEdadChange(it) },
                            isError = uiState.edadError != null,
                            errorMessage = uiState.edadError
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Dropdown de selección para Grupo Sanguíneo
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Grupo Sanguíneo",
                                color = if (uiState.grupoSanguineoError != null) MaterialTheme.colorScheme.error else FollDarkBlue,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                            )
                            
                            var expanded by remember { mutableStateOf(false) }
                            val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
                            
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = uiState.grupoSanguineo,
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = { Text("Selecciona tipo", color = Color.Gray.copy(alpha = 0.5f)) },
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
                            if (uiState.grupoSanguineoError != null) {
                                Text(
                                    text = uiState.grupoSanguineoError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo DNI (solo números, máx 8 caracteres)
                        FollTextField(
                            label = "DNI",
                            placeholder = "DNI (solo números)",
                            value = uiState.dni,
                            onValueChange = { viewModel.onDniChange(it) },
                            isError = uiState.dniError != null,
                            errorMessage = uiState.dniError
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo Enfermedades
                        FollTextField(
                            label = "Enfermedades",
                            placeholder = "Condiciones médicas (ej. Presión alta)",
                            value = uiState.enfermedades,
                            onValueChange = { viewModel.onEnfermedadesChange(it) },
                            isError = uiState.enfermedadesError != null,
                            errorMessage = uiState.enfermedadesError
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo Medicamentos (opcional)
                        FollTextField(
                            label = "Medicamentos",
                            placeholder = "Medicamentos (opcional)",
                            value = uiState.medicamentos,
                            onValueChange = { viewModel.onMedicamentosChange(it) }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Botón Crear Abuelito
                        Button(
                            onClick = { viewModel.saveAbuelito() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            Text("Crear Abuelito", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
