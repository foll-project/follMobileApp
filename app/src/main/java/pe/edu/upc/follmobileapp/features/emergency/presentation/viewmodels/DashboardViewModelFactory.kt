package pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.edu.upc.follmobileapp.features.care.data.di.CareDataModule
import pe.edu.upc.follmobileapp.features.emergency.data.di.EmergencyModule
import pe.edu.upc.follmobileapp.features.iam.data.di.DataModule

class DashboardViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(
                patientRepository = CareDataModule.providePatientRepository(context),
                emergencyRepository = EmergencyModule.provideRepository(context),
                authRepository = DataModule.provideAuthRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
