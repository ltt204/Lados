package org.nullgroup.lados.navigations

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.nullgroup.lados.screens.Screen


@Composable
fun StaffGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Staff.Reports.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        Screen.Staff.getAllScreens().forEach { screen ->
            composable(screen.route) {
                when (screen.route) {
                    Screen.Staff.ChatScreen.route -> {
                        // ChatScreen()
                    }

                    Screen.Staff.Reports.route -> {
                        // Reports()
                    }

                    Screen.Staff.TeamAnalytics.route -> {
                        // TeamAnalytics()
                    }
                }
            }
        }
    }
}

