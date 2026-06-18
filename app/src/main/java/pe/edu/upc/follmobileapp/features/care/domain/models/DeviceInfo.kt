package pe.edu.upc.follmobileapp.features.care.domain.models

data class DeviceInfo(
    val id: String,
    val batteryPercentage: Int,
    val isCharging: Boolean,
    val status: String,
    val ultimoReporte: String
)
