package pe.edu.upc.follmobileapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pe.edu.upc.follmobileapp.features.iam.presentation.views.WelcomeScreen
import pe.edu.upc.follmobileapp.features.iam.presentation.views.LoginScreen
import pe.edu.upc.follmobileapp.features.iam.presentation.views.RegisterScreen
import pe.edu.upc.follmobileapp.features.emergency.presentation.views.DashboardScreen
import pe.edu.upc.follmobileapp.features.iam.presentation.views.ProfileScreen
import pe.edu.upc.follmobileapp.features.care.presentation.views.CareScreen
import pe.edu.upc.follmobileapp.features.communication.presentation.views.SolicitudesScreen
import pe.edu.upc.follmobileapp.features.emergency.presentation.views.AlertDetailScreen
import pe.edu.upc.follmobileapp.features.emergency.presentation.views.AlertsScreen
import pe.edu.upc.follmobileapp.features.emergency.presentation.views.HistoryScreen
import pe.edu.upc.follmobileapp.features.care.presentation.views.CrearAbuelitoScreen
import pe.edu.upc.follmobileapp.features.care.presentation.views.AbuelitoDetailScreen
import pe.edu.upc.follmobileapp.features.care.presentation.views.CuidadoresScreen
import pe.edu.upc.follmobileapp.features.care.presentation.views.AnotacionesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Ahora la app arranca en la pantalla Welcome
    NavHost(navController = navController, startDestination = Routes.Welcome.route) {

        composable(Routes.Welcome.route) {
            WelcomeScreen(navController = navController)
        }

        composable(Routes.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Routes.Register.route) {
            RegisterScreen(navController = navController)
        }

        composable(Routes.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        composable(Routes.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Routes.Care.route) {
            CareScreen(navController = navController)
        }
        
        composable(Routes.Solicitudes.route) {
            SolicitudesScreen(navController = navController)
        }
        
        composable(Routes.CrearAbuelito.route) {
            CrearAbuelitoScreen(navController = navController)
        }

        composable(Routes.History.route) {
            HistoryScreen(navController = navController)
        }
        composable(Routes.Alerts.route) {
            AlertsScreen(navController = navController)
        }

        composable(Routes.AlertDetail.route + "/{alertId}") { backStackEntry ->
            val alertIdStr = backStackEntry.arguments?.getString("alertId") ?: "1"
            val alertId = alertIdStr.toLongOrNull() ?: 1L
            AlertDetailScreen(navController = navController, alertId = alertId)
        }
        
        composable(Routes.AbuelitoDetail.route + "/{patientId}") { backStackEntry ->
            val patientIdStr = backStackEntry.arguments?.getString("patientId") ?: "1"
            val patientId = patientIdStr.toLongOrNull() ?: 1L
            AbuelitoDetailScreen(navController = navController, patientId = patientId)
        }
        
        composable(Routes.Cuidadores.route + "/{patientId}") { backStackEntry ->
            val patientIdStr = backStackEntry.arguments?.getString("patientId") ?: "1"
            val patientId = patientIdStr.toLongOrNull() ?: 1L
            CuidadoresScreen(navController = navController, patientId = patientId)
        }
        
        composable(Routes.Anotaciones.route + "/{patientId}") { backStackEntry ->
            val patientIdStr = backStackEntry.arguments?.getString("patientId") ?: "1"
            val patientId = patientIdStr.toLongOrNull() ?: 1L
            AnotacionesScreen(navController = navController, patientId = patientId)
        }
    }
}