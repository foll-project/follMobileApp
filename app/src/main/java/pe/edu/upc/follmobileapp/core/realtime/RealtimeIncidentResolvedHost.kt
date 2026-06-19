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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

/**
 * Overlay global que muestra un banner elegante y efímero cuando OTRO cuidador
 * atiende una caída. Vive a nivel de MainActivity, así que aparece sin importar
 * en qué vista esté el usuario (Inicio, Mis Abuelitos, Alertas, Registro...).
 */
@Composable
fun RealtimeIncidentResolvedHost() {
    var current by remember { mutableStateOf<IncidentResolvedUiEvent?>(null) }

    LaunchedEffect(Unit) {
        RealtimeUiEvents.incidentResolved.collect { event ->
            // Si fui yo quien atendió, ya recibí confirmación; no me auto-aviso.
            if (event.byMe) return@collect
            current = event
            delay(BANNER_DURATION_MS)
            current = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(10f),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = current != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
        ) {
            current?.let { event -> IncidentResolvedBanner(event) }
        }
    }
}

@Composable
private fun IncidentResolvedBanner(event: IncidentResolvedUiEvent) {
    val accent = if (event.isFalseAlarm) Color(0xFF1565C0) else Color(0xFF2E7D32)
    val title = if (event.isFalseAlarm) "Falsa alarma" else "Emergencia atendida"
    val message = if (event.isFalseAlarm) {
        "La caída de ${event.patientName} se cerró como falsa alarma."
    } else {
        "${event.resolvedByName} está atendiendo la caída de ${event.patientName}."
    }
    val icon = if (event.isFalseAlarm) Icons.Default.Info else Icons.Default.CheckCircle

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.5.dp, accent.copy(alpha = 0.35f)),
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(10.dp, RoundedCornerShape(22.dp), ambientColor = accent, spotColor = accent)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = accent.copy(alpha = 0.12f),
                modifier = Modifier.size(46.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.padding(11.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = message,
                    fontSize = 13.sp,
                    color = Color(0xFF37474F),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

private const val BANNER_DURATION_MS = 5000L
