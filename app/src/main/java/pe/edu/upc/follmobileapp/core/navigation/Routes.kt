package pe.edu.upc.follmobileapp.core.navigation

sealed class Routes(val route: String) {
    object Welcome : Routes("welcome_screen")
    object Login : Routes("login_screen")
    object Register : Routes("register_screen")
    object Dashboard : Routes("dashboard_screen")
    object Profile : Routes("profile_screen")
    object Care : Routes("care_screen")

    object History : Routes("history_screen")
    object Alerts : Routes("alerts_screen")
    object AlertDetail : Routes("alert_detail_screen")
    object Solicitudes : Routes("solicitudes_screen")
    object CrearAbuelito : Routes("crear_abuelito_screen")
    object AbuelitoDetail : Routes("abuelito_detail_screen")
}