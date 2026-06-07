package pe.edu.upc.follmobileapp.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Mapeamos los colores de Foll al esquema de Material 3
private val FollColorScheme = lightColorScheme(
    primary = FollPrimary,
    onPrimary = White,
    primaryContainer = FollLightGreen,
    onPrimaryContainer = FollDarkBlue,
    secondary = FollDarkBlue,
    onSecondary = White,
    background = FollBackground,
    surface = White,
    onSurface = FollDarkGray,
    error = FollOrange // Usaremos este naranja para errores/alertas medias
)

@Composable
fun FollMobileAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Por ahora desactivamos los colores dinámicos de Android 12+ para mantener la identidad visual de Foll
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = FollColorScheme
    val view = LocalView.current

    // Esto pinta la barra de estado superior del celular (donde está la hora y la batería)
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        // typography = Typography, // Lo descomentaremos cuando hagamos Type.kt
        content = content
    )
}