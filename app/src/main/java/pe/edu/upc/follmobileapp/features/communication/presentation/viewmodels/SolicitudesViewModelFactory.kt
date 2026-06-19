package pe.edu.upc.follmobileapp.features.communication.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.edu.upc.follmobileapp.features.communication.data.di.CommunicationModule

class SolicitudesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SolicitudesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SolicitudesViewModel(
                repository = CommunicationModule.provideRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
