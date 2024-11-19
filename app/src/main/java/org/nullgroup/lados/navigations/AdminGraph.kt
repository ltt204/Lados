package org.nullgroup.lados.navigations

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.HomeScreen


@Composable
fun AdminGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Admin.AdminPanel.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        Screen.Admin.getAllScreens().forEach { screen ->
            composable(screen.route) {
                when (screen.route) {
                    Screen.Admin.AdminPanel.route -> {
                        // AdminPanel()
                    }

                    Screen.Admin.UserManagement.route -> {
                        // UserManagement()
                    }

                    Screen.Admin.SystemSettings.route -> {
                        // SystemSettings()
                    }

                    Screen.Admin.Analytics.route -> {
                        // Analytics()
                    }
                }
            }
        }
    }
}