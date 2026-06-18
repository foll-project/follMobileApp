package pe.edu.upc.follmobileapp.features.iam.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow
import pe.edu.upc.follmobileapp.features.iam.domain.models.User
import pe.edu.upc.follmobileapp.features.iam.domain.repository.AuthRepository

class WelcomeViewModel(
    authRepository: AuthRepository
) : ViewModel() {
    val loggedInUser: Flow<User?> = authRepository.getLoggedInUser()
}

class WelcomeViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return WelcomeViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
