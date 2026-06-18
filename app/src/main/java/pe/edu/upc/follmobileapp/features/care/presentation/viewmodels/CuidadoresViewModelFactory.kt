package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.edu.upc.follmobileapp.features.care.data.di.CareDataModule

class CuidadoresViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CuidadoresViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CuidadoresViewModel(
                patientRepository = CareDataModule.providePatientRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
