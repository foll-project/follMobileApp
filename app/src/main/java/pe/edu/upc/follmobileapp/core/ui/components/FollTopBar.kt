package pe.edu.upc.follmobileapp.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pe.edu.upc.follmobileapp.core.navigation.Routes
import pe.edu.upc.follmobileapp.core.ui.theme.FollDarkBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollTopBar(
    navController: NavController,
    hasNotification: Boolean = false,
    showBackButton: Boolean = false
) {
    CenterAlignedTopAppBar(
        title = { Text("Foll", fontWeight = FontWeight.Bold, color = FollDarkBlue, fontSize = 26.sp) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = FollDarkBlue)
                }
            } else {
                IconButton(onClick = { }) { Icon(Icons.Default.MedicalServices, contentDescription = "Logo", tint = FollDarkBlue) }
            }
        },
        actions = {
            if (!showBackButton) {
                IconButton(onClick = { navController.navigate(Routes.Solicitudes.route) }) {
                    BadgedBox(
                        badge = { if (hasNotification) Badge(containerColor = Color.Red) }
                    ) { Icon(Icons.Default.Inbox, contentDescription = "Solicitudes", tint = FollDarkBlue) }
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
    )
}
