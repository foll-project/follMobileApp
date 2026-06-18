package pe.edu.upc.follmobileapp.features.devicemanagement.data.remote.models

data class DeviceResponseDto(
    val deviceId: Int?,
    val isLinked: Boolean?,
    val status: String?,
    val connectivityStatus: String?,
    val currentBatteryLevel: Int?,
    val isCharging: Boolean?,
    val lastHeartbeatAt: String?,
    val isOnline: Boolean?,
    val isLowBattery: Boolean?,
    val firmwareVersion: String?
)

data class LinkDeviceRequest(
    val patientId: Long
)
