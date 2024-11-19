package org.nullgroup.lados.navigations

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.common.LoginScreen

@Composable
fun RoleBasedNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Common.LoginScreen.route
) {

    NavHost(navController = navController, startDestination = startDestination) {
        Screen.Common.getAllScreens().forEach { screen ->
            composable(screen.route) {
                when (screen.route) {
                    Screen.Common.LoginScreen.route -> {
                        LoginScreen()
                    }

                    Screen.Common.RegisterScreen.route -> {
                        // RegisterScreen()
                    }
                }
            }
        }
    }
}