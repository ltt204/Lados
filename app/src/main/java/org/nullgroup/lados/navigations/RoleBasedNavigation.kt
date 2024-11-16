package org.nullgroup.lados.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.screens.Screen

@Composable
fun RoleBasedNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Common.LoginScreen.route,
    userRole: UserRole,
) {
    NavHost(navController = navController, startDestination = startDestination ) {
        Screen.Common.getAllScreens().forEach {screen ->
            composable(screen.route) {
                when(screen.route) {
                    Screen.Common.LoginScreen.route -> {
                        // LoginScreen()
                    }
                    Screen.Common.RegisterScreen.route -> {
                        // RegisterScreen()
                    }
                }
            }
        }

        if(userRole == UserRole.ADMIN) {
            Screen.Admin.getAllScreens().forEach {screen ->
                composable(screen.route) {
                    when(screen.route) {
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

        if(userRole == UserRole.STAFF || userRole == UserRole.ADMIN) {
            Screen.Staff.getAllScreens().forEach {screen ->
                composable(screen.route) {
                    when(screen.route) {
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

        if(userRole == UserRole.CUSTOMER) {
            Screen.Customer.getAllScreens().forEach {screen ->
                composable(screen.route) {
                    when(screen.route) {
                        Screen.Customer.HomeScreen.route -> {
                            // HomeScreen()
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
}