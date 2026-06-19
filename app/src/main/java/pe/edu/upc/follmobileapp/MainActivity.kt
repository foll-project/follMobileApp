package pe.edu.upc.follmobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import pe.edu.upc.follmobileapp.core.di.RealtimeModule
import pe.edu.upc.follmobileapp.core.navigation.AppNavigation
import pe.edu.upc.follmobileapp.core.realtime.RealtimeIncidentResolvedHost
import pe.edu.upc.follmobileapp.core.ui.theme.FollMobileAppTheme
import pe.edu.upc.follmobileapp.features.iam.data.di.DataModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FollMobileAppTheme {
                // Surface es un contenedor que toma el color de fondo de nuestro Theme (FollBackground)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RealtimeConnector() // Gestiona la conexión WebSocket según la sesión
                    AppNavigation() // Iniciamos la navegación
                    RealtimeIncidentResolvedHost() // Banner global cuando alguien atiende una caída
                }
            }
        }
    }
}

/**
 * Mantiene la conexión SignalR sincronizada con la sesión del usuario:
 *  - Cuando hay un usuario logueado, abre la conexión en tiempo real con su token.
 *  - Cuando se cierra sesión (usuario nulo), corta la conexión.
 */
@Composable
private fun RealtimeConnector() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val authRepository = DataModule.provideAuthRepository(context)
        val realtimeService = RealtimeModule.provideNotificationRealtimeService(context)

        authRepository.getLoggedInUser().collect { user ->
            val token = user?.token
            if (!token.isNullOrBlank()) {
                realtimeService.start(token)
            } else {
                realtimeService.stop()
            }
        }
    }
}
