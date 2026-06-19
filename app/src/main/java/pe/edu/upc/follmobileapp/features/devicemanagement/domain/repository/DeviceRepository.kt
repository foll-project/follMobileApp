package pe.edu.upc.follmobileapp.features.devicemanagement.domain.repository

import pe.edu.upc.follmobileapp.features.devicemanagement.domain.models.FollDevice

interface DeviceRepository {
    suspend fun linkDevice(deviceId: Int, patientId: Long): Result<Unit>
    suspend fun unlinkDevice(deviceId: Int): Result<Unit>
    suspend fun getDeviceByPatient(patientId: Long): Result<FollDevice?>
}
