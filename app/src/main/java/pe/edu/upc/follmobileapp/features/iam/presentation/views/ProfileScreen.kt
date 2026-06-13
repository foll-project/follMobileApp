package pe.edu.upc.follmobileapp.features.iam.presentation.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.ui.components.FollBottomBar
import pe.edu.upc.follmobileapp.core.ui.components.FollTopBar
import pe.edu.upc.follmobileapp.core.ui.theme.*
import pe.edu.upc.follmobileapp.features.iam.presentation.viewmodels.ProfileSubScreen
import pe.edu.upc.follmobileapp.features.iam.presentation.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
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
        topBar = { FollTopBar(navController) },
        bottomBar = { FollBottomBar(navController, "profile_screen") },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            when (uiState.currentSubScreen) {
                ProfileSubScreen.MAIN -> {
                    ProfileMainContent(
                        paddingValues = paddingValues,
                        firstName = uiState.firstName,
                        lastName = uiState.lastName,
                        onNavigateToUpdate = { viewModel.navigateTo(ProfileSubScreen.UPDATE_DATA) },
                        onNavigateToHelp = { viewModel.navigateTo(ProfileSubScreen.HELP_SUPPORT) }
                    )
                }
                ProfileSubScreen.UPDATE_DATA -> {
                    UpdateDataContent(
                        paddingValues = paddingValues,
                        uiState = uiState,
                        onFirstNameChange = { viewModel.onFirstNameChanged(it) },
                        onLastNameChange = { viewModel.onLastNameChanged(it) },
                        onEmailChange = { viewModel.onEmailChanged(it) },
                        onPhoneChange = { viewModel.onPhoneChanged(it) },
                        onPasswordChange = { viewModel.onPasswordChanged(it) },
                        onConfirmPasswordChange = { viewModel.onConfirmPasswordChanged(it) },
                        onSave = { viewModel.updateProfile() },
                        onCancel = { viewModel.navigateTo(ProfileSubScreen.MAIN) }
                    )
                }
                ProfileSubScreen.HELP_SUPPORT -> {
                    HelpSupportContent(
                        paddingValues = paddingValues,
                        onBack = { viewModel.navigateTo(ProfileSubScreen.MAIN) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileMainContent(
    paddingValues: PaddingValues,
    firstName: String,
    lastName: String,
    onNavigateToUpdate: () -> Unit,
    onNavigateToHelp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Avatar / Iniciales
        Surface(
            shape = CircleShape,
            color = FollDarkBlue,
            modifier = Modifier
                .size(90.dp)
                .shadow(6.dp, CircleShape, ambientColor = FollDarkBlue, spotColor = FollDarkBlue)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "${firstName.firstOrNull() ?: 'M'}${lastName.firstOrNull() ?: 'G'}",
                    color = White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Información de Usuario
        Text("$firstName $lastName", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Cuidador de Confianza", fontSize = 15.sp, color = FollDarkBlue, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(40.dp))

        // Lista de Opciones - Solo dos opciones habilitadas
        ProfileOptionCard(
            icon = Icons.Default.Person,
            iconBgColor = FollLightGreen.copy(alpha = 0.5f),
            title = "Actualizar Mis Datos",
            subtitle = "Información personal y de contacto",
            onClick = onNavigateToUpdate
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProfileOptionCard(
            icon = Icons.Default.HelpOutline,
            iconBgColor = FollYellow,
            title = "Ayuda y Soporte",
            subtitle = "Preguntas frecuentes y soporte técnico",
            onClick = onNavigateToHelp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Botón Cerrar Sesión
        TextButton(
            onClick = { /* Lógica de Cerrar Sesión */ },
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = FollDarkBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
        }
    }
}

@Composable
fun UpdateDataContent(
    paddingValues: PaddingValues,
    uiState: pe.edu.upc.follmobileapp.features.iam.presentation.viewmodels.ProfileUiState,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onCancel() }
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Retroceder", tint = FollDarkBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver al perfil", color = FollDarkBlue, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Actualizar Mis Datos", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Modifica tus credenciales y datos de contacto", fontSize = 15.sp, color = FollDarkBlue)

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = White,
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
            Column(modifier = Modifier.padding(24.dp)) {
                // Nombre
                Text("Nombre", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.firstName,
                    onValueChange = onFirstNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.firstNameError != null,
                    shape = RoundedCornerShape(12.dp)
                )
                if (uiState.firstNameError != null) {
                    Text(uiState.firstNameError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Apellido
                Text("Apellido", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.lastName,
                    onValueChange = onLastNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.lastNameError != null,
                    shape = RoundedCornerShape(12.dp)
                )
                if (uiState.lastNameError != null) {
                    Text(uiState.lastNameError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Correo Electrónico
                Text("Correo Electrónico", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.emailError != null,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                if (uiState.emailError != null) {
                    Text(uiState.emailError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Celular
                Text("Celular", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.phoneNumber,
                    onValueChange = onPhoneChange,
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.phoneNumberError != null,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                if (uiState.phoneNumberError != null) {
                    Text(uiState.phoneNumberError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contraseña
                Text("Nueva Contraseña", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.passwordError != null,
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                if (uiState.passwordError != null) {
                    Text(uiState.passwordError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirmar Contraseña
                Text("Confirmar Contraseña", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.confirmPasswordError != null,
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                if (uiState.confirmPasswordError != null) {
                    Text(uiState.confirmPasswordError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Acciones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onSave,
                        colors = ButtonDefaults.buttonColors(containerColor = FollYellow),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Guardar", color = FollDarkBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    OutlinedButton(
                        onClick = onCancel,
                        border = BorderStroke(1.dp, FollDarkBlue),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar", color = FollDarkBlue, fontSize = 15.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun HelpSupportContent(
    paddingValues: PaddingValues,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBack() }
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Retroceder", tint = FollDarkBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver al perfil", color = FollDarkBlue, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Ayuda y Soporte", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = FollDarkBlue)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Preguntas frecuentes de cuidado y contacto de soporte", fontSize = 15.sp, color = FollDarkBlue)

        Spacer(modifier = Modifier.height(24.dp))

        // Preguntas Frecuentes (FAQ Block)
        Text("Preguntas Frecuentes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))

        FaqAccordionItem(
            question = "¿Cuál es la diferencia entre un Cuidador Principal y un contacto de emergencia?",
            answer = "Como Cuidador Principal (o Invitado), tienes un monitoreo integral: recibirás las alertas de caídas, pero también notificaciones sobre el estado del dispositivo del adulto mayor (si la batería está baja o si se desconecta de la red). Los demás contactos de emergencia de la lista solo serán notificados estrictamente cuando ocurra un accidente."
        )
        Spacer(modifier = Modifier.height(12.dp))

        FaqAccordionItem(
            question = "¿Qué debo hacer si recibo una notificación de \"dispositivo desconectado\" o \"batería baja\"?",
            answer = "Estas notificaciones preventivas te avisan que la protección podría interrumpirse. Lo ideal es que te comuniques con el adulto mayor para recordarle que cargue su celular o verifiques si hubo algún problema con su conexión a internet, asegurando así que el monitoreo siga activo."
        )
        Spacer(modifier = Modifier.height(12.dp))

        FaqAccordionItem(
            question = "¿Qué pasa si la aplicación detecta un movimiento brusco que no es una caída real?",
            answer = "Antes de enviarte una alerta de emergencia, la aplicación emitirá un sonido de advertencia en el celular del usuario. Ellos tendrán unos segundos para presionar el botón de \"Cancelar\" en su pantalla. Si no lo cancelan a tiempo, recibirás la alerta inmediatamente."
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Datos de Contacto de Soporte
        Text("Contacto de Soporte", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = White,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = FollPrimary.copy(alpha = 0.2f), modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Phone, contentDescription = "Teléfono", tint = FollDarkBlue, modifier = Modifier.padding(10.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Teléfono", fontSize = 12.sp, color = Color.Gray)
                        Text("962222408", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = FollPrimary.copy(alpha = 0.2f), modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Email, contentDescription = "Correo", tint = FollDarkBlue, modifier = Modifier.padding(10.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Correo Electrónico", fontSize = 12.sp, color = Color.Gray)
                        Text("soporte@foll.com", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun FaqAccordionItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = White,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false,
                ambientColor = FollDarkBlue,
                spotColor = FollDarkBlue
            )
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FollDarkBlue,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = FollDarkBlue
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = answer,
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun ProfileOptionCard(icon: ImageVector, iconBgColor: Color, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = White,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false,
                ambientColor = FollDarkBlue,
                spotColor = FollDarkBlue
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = iconBgColor,
                modifier = Modifier.size(50.dp)
            ) {
                Icon(icon, contentDescription = null, tint = FollDarkBlue, modifier = Modifier.padding(12.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(subtitle, fontSize = 14.sp, color = FollDarkBlue)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = "Ir", tint = Color.LightGray)
        }
    }
}