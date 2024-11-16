package org.nullgroup.lados.navigations

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.HomeScreen

@Composable
fun CustomerGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Customer.HomeScreen.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        Screen.Customer.getAllScreens().forEach { screen ->
            composable(screen.route) {
                when (screen.route) {
                    Screen.Customer.HomeScreen.route -> {
                        HomeScreen()
                    }

                    Screen.Customer.ChatScreen.route -> {
                        // ChatScreen()
                    }

                    Screen.Customer.Profile.route -> {
                        // Profile()
                    }

                    Screen.Customer.Tasks.route -> {
                        // Tasks()
                    }
                }
            }
        }
    }
}