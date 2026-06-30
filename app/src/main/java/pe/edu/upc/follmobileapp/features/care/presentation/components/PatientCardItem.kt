package pe.edu.upc.follmobileapp.features.care.presentation.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.CaregiverRole
import pe.edu.upc.follmobileapp.features.care.presentation.viewmodels.PatientUiModel
import pe.edu.upc.follmobileapp.core.ui.theme.*

@Composable
fun PatientCardItem(
    patient: PatientUiModel,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onVerPerfil: () -> Unit,
    onVerCuidadores: () -> Unit,
    onVerAnotaciones: () -> Unit
) {
    val context = LocalContext.current
    var showQrDialog by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(32.dp),
        color = Color(0xFBFFFFFF),
        border = if (patient.isInEmergency) BorderStroke(2.dp, Color(0xFFEF5350)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (patient.isInEmergency) 10.dp else 6.dp,
                shape = RoundedCornerShape(32.dp),
                clip = false,
                ambientColor = if (patient.isInEmergency) Color(0xFFEF5350) else FollDarkBlue,
                spotColor = if (patient.isInEmergency) Color(0xFFEF5350) else FollDarkBlue
            )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Fila Principal: Nombre y Estado del Dispositivo (LED Encendido/Apagado con texto)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = patient.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    if (patient.isInEmergency) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF5350))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Emergencia",
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "¡EMERGENCIA ACTIVA!",
                                    color = Color(0xFFE53935),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Indicador LED con texto e ilusión de brillo (sutil halo y brillo de vidrio)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(18.dp)
                    ) {
                        if (patient.isDeviceOn) {
                            // Halo exterior de luz led encendida (más sutil)
                            Surface(shape = CircleShape, color = FollPrimary.copy(alpha = 0.25f), modifier = Modifier.size(18.dp)) {}
                            // Núcleo del LED
                            Surface(shape = CircleShape, color = FollPrimary, modifier = Modifier.size(10.dp)) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.TopStart
                                ) {
                                    // Reflejo de brillo del lente del LED
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.75f),
                                        modifier = Modifier
                                            .padding(start = 2.dp, top = 2.dp)
                                            .size(2.5.dp)
                                    ) {}
                                }
                            }
                        } else {
                            Surface(shape = CircleShape, color = Color.Gray.copy(alpha = 0.1f), modifier = Modifier.size(14.dp)) {}
                            Surface(shape = CircleShape, color = Color.Gray, modifier = Modifier.size(10.dp)) {}
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (patient.isDeviceOn) "Encendido" else "Apagado",
                        color = if (patient.isDeviceOn) FollDarkBlue else Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tag/Badge de Rol de Cuidador
            Surface(
                color = when (patient.role) {
                    CaregiverRole.OFFICIAL_GUARDIAN -> FollYellow.copy(alpha = 0.5f)
                    CaregiverRole.SECONDARY_GUARDIAN -> FollLightGreen.copy(alpha = 0.5f)
                    CaregiverRole.INVITED_GUARDIAN -> FollPaleYellow.copy(alpha = 0.5f)
                },
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = when (patient.role) {
                            CaregiverRole.OFFICIAL_GUARDIAN -> Icons.Default.Star
                            else -> Icons.Default.People
                        },
                        contentDescription = null,
                        tint = when (patient.role) {
                            CaregiverRole.OFFICIAL_GUARDIAN -> FollOrange
                            else -> FollDarkBlue
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = patient.role.label,
                        fontSize = 14.sp,
                        color = FollDarkBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detalles de Hardware e ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ID Hardware
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeveloperBoard, contentDescription = "Hardware", tint = FollDarkGray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "ID: ${patient.deviceId}", fontSize = 15.sp, color = FollDarkGray)
                }

                // Estado de la Batería
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (patient.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                        contentDescription = "Batería",
                        tint = if (patient.isCharging) FollPrimary else FollDarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${patient.batteryPercentage}%" + if (patient.isCharging) " (Cargando)" else "",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = FollDarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animación para mostrar/ocultar los botones de detalles de acciones
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Botones más grandes organizados en 2 filas
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onVerPerfil,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ver Perfil", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            Button(
                                onClick = onVerAnotaciones,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FollLightGreen),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = FollDarkBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Anotaciones", color = FollDarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onVerCuidadores,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, FollDarkBlue),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = FollDarkBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cuidadores", color = FollDarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            Button(
                                onClick = { showQrDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, FollDarkBlue),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = FollDarkBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ver QR", color = FollDarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Botón Desplegar / Contraer Acciones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Ocultar acciones" else "Ver acciones",
                    color = FollDarkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = FollDarkBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            confirmButton = {
                Button(
                    onClick = { showQrDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FollDarkBlue),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "QR de Cuidado - ${patient.name}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FollDarkBlue
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Escanea este código desde la app móvil del nuevo cuidador para vincularlo instantáneamente a este paciente.",
                        fontSize = 14.sp,
                        color = FollDarkGray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "foll:patient:${patient.id}",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    val qrContent = "foll:patient:${patient.id}"
                    val qrMatrix = remember(qrContent) {
                        try {
                            val writer = com.google.zxing.qrcode.QRCodeWriter()
                            // Genera una matriz lógica (los píxeles activos) sin escalado prematuro
                            writer.encode(qrContent, com.google.zxing.BarcodeFormat.QR_CODE, 512, 512)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    Canvas(
                        modifier = Modifier
                            .size(200.dp) // Tamaño adecuado para lectura
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp) // Zona de Silencio (Quiet Zone) requerida por el estándar
                    ) {
                        qrMatrix?.let { matrix ->
                            val width = matrix.width
                            val height = matrix.height
                            val cellWidth = size.width / width
                            val cellHeight = size.height / height

                            for (x in 0 until width) {
                                for (y in 0 until height) {
                                    if (matrix.get(x, y)) {
                                        drawRect(
                                            color = Color.Black, // Forzar color negro estricto para máximo contraste
                                            topLeft = Offset(x * cellWidth, y * cellHeight),
                                            size = Size(cellWidth + 0.5f, cellHeight + 0.5f) // El +0.5f evita huecos por anti-aliasing
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "ID Dispositivo: ${patient.deviceId}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}
