package pe.edu.upc.follmobileapp.features.devicemanagement.data.di

import android.content.Context
import pe.edu.upc.follmobileapp.core.di.NetworkModule
import pe.edu.upc.follmobileapp.features.devicemanagement.data.remote.services.DeviceService
import pe.edu.upc.follmobileapp.features.devicemanagement.data.repository.DeviceRepositoryImpl
import pe.edu.upc.follmobileapp.features.devicemanagement.domain.repository.DeviceRepository

object DeviceModule {
    @Volatile
    private var deviceService: DeviceService? = null

    @Volatile
    private var deviceRepository: DeviceRepository? = null

    fun provideDeviceService(context: Context): DeviceService {
        return deviceService ?: synchronized(this) {
            deviceService ?: run {
                val instance = NetworkModule.provideRetrofit(context).create(DeviceService::class.java)
                deviceService = instance
                instance
            }
        }
    }

    fun provideRepository(context: Context): DeviceRepository {
        return deviceRepository ?: synchronized(this) {
            deviceRepository ?: run {
                val instance = DeviceRepositoryImpl(
                    deviceService = provideDeviceService(context)
                )
                deviceRepository = instance
                instance
            }
        }
    }
}
