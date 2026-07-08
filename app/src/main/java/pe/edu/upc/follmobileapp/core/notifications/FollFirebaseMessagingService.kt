package pe.edu.upc.follmobileapp.core.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FollFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "Nuevo token FCM: $token")
        PushRegistrationManager(applicationContext).registerTokenIfAuthenticated(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: message.data["title"].orEmpty()
        val body = message.notification?.body ?: message.data["body"].orEmpty()
        Log.i(
            TAG,
            "Push recibido: from=${message.from}, title=$title, body=$body, data=${message.data}"
        )
    }

    private companion object {
        private const val TAG = "FollPush"
    }
}
