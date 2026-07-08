package pe.edu.upc.follmobileapp.core.notifications

import android.content.Context
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pe.edu.upc.follmobileapp.features.emergency.data.di.EmergencyModule
import pe.edu.upc.follmobileapp.features.emergency.data.remote.models.PushTokenRequest
import pe.edu.upc.follmobileapp.features.iam.data.di.DataModule

class PushRegistrationManager(
    context: Context
) {
    private val appContext = context.applicationContext

    fun registerCurrentTokenIfAuthenticated() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.i(TAG, "FCM token obtenido: $token")
                registerTokenIfAuthenticated(token)
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "No se pudo obtener el token FCM", error)
            }
    }

    fun registerTokenIfAuthenticated(token: String) {
        if (token.isBlank()) {
            Log.w(TAG, "Token FCM vacio; se omite registro")
            return
        }

        scope.launch {
            try {
                val authLocalDataSource = DataModule.provideAuthLocalDataSource(appContext)
                val jwt = authLocalDataSource.getToken()
                if (jwt.isNullOrBlank()) {
                    Log.i(TAG, "No hay sesion autenticada; se omite registro de token FCM")
                    return@launch
                }

                val request = PushTokenRequest(
                    token = token,
                    platform = "Android",
                    deviceName = resolveDeviceName()
                )
                EmergencyModule.provideApiService(appContext).registerPushToken(request)
                Log.i(TAG, "Token FCM registrado en backend: platform=${request.platform}, deviceName=${request.deviceName}")
            } catch (error: Exception) {
                Log.e(TAG, "Error registrando token FCM en backend", error)
            }
        }
    }

    private fun resolveDeviceName(): String {
        val manufacturer = Build.MANUFACTURER.orEmpty().trim()
        val model = Build.MODEL.orEmpty().trim()
        return when {
            manufacturer.isBlank() && model.isBlank() -> "Android Device"
            manufacturer.isBlank() -> model
            model.isBlank() -> manufacturer
            model.startsWith(manufacturer, ignoreCase = true) -> model
            else -> "$manufacturer $model"
        }
    }

    private companion object {
        private const val TAG = "FollPush"
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}
