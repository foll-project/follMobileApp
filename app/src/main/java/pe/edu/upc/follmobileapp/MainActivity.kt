package pe.edu.upc.follmobileapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import pe.edu.upc.follmobileapp.core.di.RealtimeModule
import pe.edu.upc.follmobileapp.core.navigation.AppNavigation
import pe.edu.upc.follmobileapp.core.notifications.PushRegistrationManager
import pe.edu.upc.follmobileapp.core.ui.theme.FollMobileAppTheme
import pe.edu.upc.follmobileapp.features.iam.data.di.DataModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FollMobileAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RealtimeConnector()
                    PushNotificationsConnector()
                    AppNavigation()
                }
            }
        }
    }
}

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

@Composable
private fun PushNotificationsConnector() {
    val context = LocalContext.current
    val authRepository = remember(context) { DataModule.provideAuthRepository(context) }
    val pushRegistrationManager = remember(context) { PushRegistrationManager(context) }
    val loggedInUser by authRepository.getLoggedInUser().collectAsState(initial = null)

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.i("FollPush", "Permiso POST_NOTIFICATIONS concedido=$granted")
    }

    LaunchedEffect(loggedInUser?.userId) {
        val user = loggedInUser ?: return@LaunchedEffect

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        Log.i("FollPush", "Sesion activa detectada para userId=${user.userId}; registrando token FCM")
        pushRegistrationManager.registerCurrentTokenIfAuthenticated()
    }
}
