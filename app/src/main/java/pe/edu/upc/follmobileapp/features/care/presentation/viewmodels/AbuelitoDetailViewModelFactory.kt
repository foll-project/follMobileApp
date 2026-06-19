package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.edu.upc.follmobileapp.features.care.data.di.CareDataModule
import pe.edu.upc.follmobileapp.features.devicemanagement.data.di.DeviceModule

class AbuelitoDetailViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AbuelitoDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AbuelitoDetailViewModel(
                patientRepository = CareDataModule.providePatientRepository(context),
                deviceRepository = DeviceModule.provideRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
