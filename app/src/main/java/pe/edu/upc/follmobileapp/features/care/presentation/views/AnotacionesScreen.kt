package pe.edu.upc.follmobileapp.features.care.presentation.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
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
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.AnotacionesViewModel
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.AnotacionesViewModelFactory
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.AnnotationUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnotacionesScreen(
    navController: NavController,
    patientId: Long,
    viewModel: AnotacionesViewModel = viewModel(factory = AnotacionesViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Cargar notas al iniciar
    LaunchedEffect(patientId) {
        viewModel.loadAnnotations(patientId)
    }

    // Mostrar Toasts con mensajes de acción
    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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

                // Tarjeta Principal de Bitácora
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
                            Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = FollDarkBlue, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Bitácora de Cuidado",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FollDarkBlue
                                )
                                Text(
                                    text = "Historial de anotaciones médicas para ${uiState.patientName}.",
                                    fontSize = 14.sp,
                                    color = FollDarkGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Campo de texto para añadir nueva anotación
                        OutlinedTextField(
                            value = uiState.newAnnotationText,
                            onValueChange = { viewModel.onNewAnnotationTextChange(it) },
                            placeholder = { Text("Escribe una nueva anotación médica...", color = Color.Gray.copy(alpha = 0.5f)) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = FollDarkBlue,
                                unfocusedIndicatorColor = Color.LightGray,
                                errorContainerColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Botón de Añadir Nueva Anotación (color dorado/amarillo premium de Foll)
                        Button(
                            onClick = { viewModel.publishAnnotation() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FollYellow),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Text(
                                text = "Añadir nueva anotación",
                                color = FollDarkBlue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Lista de anotaciones médicas
                        if (uiState.annotations.isEmpty()) {
                            Text(
                                text = "No hay anotaciones registradas.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else {
                            uiState.annotations.forEach { annotation ->
                                AnnotationItemRow(annotation = annotation)
                                Spacer(modifier = Modifier.height(12.dp))
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
fun AnnotationItemRow(annotation: AnnotationUiModel) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFFDF1), // Fondo color de la app que coincide con el mockup
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Fecha e información de autor
            Text(
                text = "${annotation.dateString} - ${annotation.authorName}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = FollDarkGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            // Contenido de la nota médica
            Text(
                text = annotation.content,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        }
    }
}
