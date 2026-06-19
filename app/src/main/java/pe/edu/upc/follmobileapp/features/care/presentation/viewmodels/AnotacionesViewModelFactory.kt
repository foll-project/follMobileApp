package pe.edu.upc.follmobileapp.features.care.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.edu.upc.follmobileapp.features.care.data.di.CareDataModule
import pe.edu.upc.follmobileapp.features.iam.data.di.DataModule

class AnotacionesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnotacionesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnotacionesViewModel(
                patientRepository = CareDataModule.providePatientRepository(context),
                authRepository = DataModule.provideAuthRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
