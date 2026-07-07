package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.edu.upc.follmobileapp.features.care.data.di.CareDataModule

class CuidadoresViewModelFactory(
    private val context: Context,
    private val patientId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CuidadoresViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CuidadoresViewModel(
                patientId = patientId,
                repository = CareDataModule.providePatientRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
