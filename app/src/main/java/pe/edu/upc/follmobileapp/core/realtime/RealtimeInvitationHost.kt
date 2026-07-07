package pe.edu.upc.follmobileapp.core.realtime

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import pe.edu.upc.follmobileapp.core.navigation.Routes
import pe.edu.upc.follmobileapp.core.ui.theme.FollDarkBlue

/**
 * Toast global de invitaciones en tiempo real (nueva solicitud, aceptada o rechazada).
 * Vive a nivel de MainActivity para aparecer en cualquier pantalla.
 */
@Composable
fun RealtimeInvitationHost(navController: NavController) {
    var current by remember { mutableStateOf<InvitationChangedUiEvent?>(null) }

    LaunchedEffect(Unit) {
        RealtimeUiEvents.invitationChanged.collect { event ->
            current = event
            delay(BANNER_DURATION_MS)
            if (current == event) current = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(9f),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = current != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            current?.let { event ->
                InvitationChangedBanner(
                    event = event,
                    onDismiss = { current = null },
                    onViewInvitations = {
                        current = null
                        navController.navigate(Routes.Solicitudes.route)
                    }
                )
            }
        }
    }
}

@Composable
private fun InvitationChangedBanner(
    event: InvitationChangedUiEvent,
    onDismiss: () -> Unit,
    onViewInvitations: () -> Unit
) {
    val style = invitationStyle(event.kind)

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, style.accent.copy(alpha = 0.35f)),
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .shadow(10.dp, RoundedCornerShape(22.dp), ambientColor = style.accent, spotColor = style.accent)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Surface(
                    shape = CircleShape,
                    color = style.accent.copy(alpha = 0.12f),
                    modifier = Modifier.size(46.dp)
                ) {
                    Icon(
                        imageVector = style.icon,
                        contentDescription = null,
                        tint = style.accent,
                        modifier = Modifier.padding(11.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = style.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF90A4AE),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = event.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FollDarkBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.message,
                        fontSize = 13.sp,
                        color = Color(0xFF37474F),
                        lineHeight = 18.sp
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFFB0BEC5))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End) {
                TextButton(onClick = onDismiss) {
                    Text("Descartar", color = Color(0xFF78909C), fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    onClick = onViewInvitations,
                    colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ver invitaciones", fontSize = 13.sp)
                }
            }
        }
    }
}

private data class InvitationBannerStyle(
    val accent: Color,
    val label: String,
    val icon: ImageVector
)

private fun invitationStyle(kind: String): InvitationBannerStyle = when (kind.lowercase()) {
    "accepted" -> InvitationBannerStyle(
        accent = Color(0xFF2E7D32),
        label = "Invitación aceptada",
        icon = Icons.Default.CheckCircle
    )
    "rejected" -> InvitationBannerStyle(
        accent = Color(0xFFC62828),
        label = "Invitación rechazada",
        icon = Icons.Default.Close
    )
    else -> InvitationBannerStyle(
        accent = Color(0xFFF9A825),
        label = "Nueva solicitud",
        icon = Icons.Default.MailOutline
    )
}

private const val BANNER_DURATION_MS = 9000L
