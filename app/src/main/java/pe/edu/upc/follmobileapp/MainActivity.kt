package pe.edu.upc.follmobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import pe.edu.upc.follmobileapp.core.navigation.AppNavigation
import pe.edu.upc.follmobileapp.core.ui.theme.FollMobileAppTheme

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
                    AppNavigation() // Iniciamos la navegación
                }
            }
        }
    }
}