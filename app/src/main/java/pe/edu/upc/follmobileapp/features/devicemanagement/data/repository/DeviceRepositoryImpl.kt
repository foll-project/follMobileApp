package pe.edu.upc.follmobileapp.features.devicemanagement.data.repository

import pe.edu.upc.follmobileapp.features.devicemanagement.data.remote.models.LinkDeviceRequest
import pe.edu.upc.follmobileapp.features.devicemanagement.data.remote.services.DeviceService
import pe.edu.upc.follmobileapp.features.devicemanagement.domain.models.FollDevice
import pe.edu.upc.follmobileapp.features.devicemanagement.domain.repository.DeviceRepository

object DeviceMapper {
    fun toDomain(dto: pe.edu.upc.follmobileapp.features.devicemanagement.data.remote.models.DeviceResponseDto): FollDevice {
        return FollDevice(
            deviceId = dto.deviceId ?: 0,
            isLinked = dto.isLinked ?: false,
            status = dto.status ?: "Offline",
            connectivityStatus = dto.connectivityStatus ?: "Wifi",
            currentBatteryLevel = dto.currentBatteryLevel ?: 0,
            isCharging = dto.isCharging ?: false,
            lastHeartbeatAt = dto.lastHeartbeatAt,
            isOnline = dto.isOnline ?: false,
            isLowBattery = dto.isLowBattery ?: false,
            firmwareVersion = dto.firmwareVersion
        )
    }
}

class DeviceRepositoryImpl(
    private val deviceService: DeviceService
) : DeviceRepository {

    override suspend fun linkDevice(deviceId: Int, patientId: Long): Result<Unit> = runCatching {
        val request = LinkDeviceRequest(patientId)
        deviceService.linkDevice(deviceId, request)
    }

    override suspend fun unlinkDevice(deviceId: Int): Result<Unit> = runCatching {
        deviceService.unlinkDevice(deviceId)
    }

    override suspend fun getDeviceByPatient(patientId: Long): Result<FollDevice?> = runCatching {
        val dto = deviceService.getDeviceByPatient(patientId)
        DeviceMapper.toDomain(dto)
    }
}
