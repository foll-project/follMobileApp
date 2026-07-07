package pe.edu.upc.follmobileapp.features.care.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
fun ScanPatientQrScreen(
    modifier: Modifier = Modifier,
    onQrScanned: (Long) -> Unit
) {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = "Escanear QR",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Vincular Familiar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Pide a tu familiar que muestre su código QR en pantalla y escanéalo para establecer el vínculo de forma inmediata.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                if (isScanning) return@Button
                isScanning = true

                val scanner = GmsBarcodeScanning.getClient(context)
                
                scanner.startScan()
                    .addOnSuccessListener { barcode ->
                        val rawValue = barcode.rawValue
                        if (rawValue != null && rawValue.startsWith("foll:patient:")) {
                            val idString = rawValue.removePrefix("foll:patient:")
                            val patientId = idString.trim().toLongOrNull()
                            
                            if (patientId != null && patientId > 0) {
                                // Invocamos el callback que hará la petición POST asíncrona
                                onQrScanned(patientId)
                            } else {
                                Toast.makeText(context, "Código QR no válido", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Código QR no válido", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnCanceledListener {
                        Toast.makeText(context, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error al escanear: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                        isScanning = false
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.large,
            enabled = !isScanning
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = if (isScanning) "Iniciando escáner..." else "Escanear QR de Familiar",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
