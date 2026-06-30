package pe.edu.upc.follmobileapp.features.iam.data.local

import android.content.Context
import kotlinx.coroutines.flow.Flow
import pe.edu.upc.follmobileapp.features.iam.data.local.dao.UserDao
import pe.edu.upc.follmobileapp.features.iam.data.local.models.UserEntity
import pe.edu.upc.follmobileapp.core.data.local.database.FollDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthLocalDataSource(
    private val userDao: UserDao,
    private val context: Context
) {
    private val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    suspend fun saveSession(user: UserEntity) {
        userDao.saveUser(user)
        sharedPrefs.edit().putString("auth_token", user.token).apply()
    }

    suspend fun clearSession() {
        withContext(Dispatchers.IO) {
            FollDatabase.getDatabase(context).clearAllTables()
        }
        sharedPrefs.edit().remove("auth_token").apply()
    }

    suspend fun getLoggedInUser(): UserEntity? {
        return userDao.getLoggedInUser()
    }

    fun getLoggedInUserFlow(): Flow<UserEntity?> {
        return userDao.getLoggedInUserFlow()
    }

    fun getToken(): String? {
        return sharedPrefs.getString("auth_token", null)
    }
}
