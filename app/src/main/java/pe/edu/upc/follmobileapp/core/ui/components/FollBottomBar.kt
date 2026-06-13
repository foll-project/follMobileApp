package pe.edu.upc.follmobileapp.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import pe.edu.upc.follmobileapp.core.navigation.Routes
import pe.edu.upc.follmobileapp.core.ui.theme.*

@Composable
fun FollBottomBar(navController: NavController, currentRoute: String) {
    Column {
        HorizontalDivider(
            color = Color.LightGray.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        NavigationBar(
            containerColor = FollBackground,
            contentColor = FollDarkBlue,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
        ) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = currentRoute == Routes.Dashboard.route,
                onClick = {
                    if (currentRoute != Routes.Dashboard.route) {
                        navController.navigate(Routes.Dashboard.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FollDarkBlue,
                    indicatorColor = FollLightGreen,
                    unselectedIconColor = FollDarkBlue
                )
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.People, contentDescription = "Care") },
                label = { Text("Care") },
                selected = currentRoute == Routes.Care.route,
                onClick = {
                    if (currentRoute != Routes.Care.route) {
                        navController.navigate(Routes.Care.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = FollDarkBlue, indicatorColor = FollLightGreen, unselectedIconColor = FollDarkBlue)
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.SnippetFolder, contentDescription = "History") },
                label = { Text("History") },
                selected = currentRoute == Routes.History.route,
                onClick = {
                    if (currentRoute != Routes.History.route) {
                        navController.navigate(Routes.History.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = FollDarkBlue, indicatorColor = FollLightGreen, unselectedIconColor = FollDarkBlue)
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Notifications, contentDescription = "Alerts") },
                label = { Text("Alerts") },
                selected = currentRoute == Routes.Alerts.route,
                onClick = {
                    if (currentRoute != Routes.Alerts.route) {
                        navController.navigate(Routes.Alerts.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = FollDarkBlue, indicatorColor = FollLightGreen, unselectedIconColor = FollDarkBlue)
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                label = { Text("Profile") },
                selected = currentRoute == Routes.Profile.route,
                onClick = {
                    if (currentRoute != Routes.Profile.route) {
                        navController.navigate(Routes.Profile.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = FollDarkBlue, indicatorColor = FollLightGreen, unselectedIconColor = FollDarkBlue)
            )
        }
    }
}