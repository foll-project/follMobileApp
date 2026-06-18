package pe.edu.upc.follmobileapp.features.emergency.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.edu.upc.follmobileapp.features.care.data.di.CareDataModule
import pe.edu.upc.follmobileapp.features.emergency.data.di.EmergencyModule

class HistoryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(
                emergencyRepository = EmergencyModule.provideRepository(context),
                patientRepository = CareDataModule.providePatientRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
