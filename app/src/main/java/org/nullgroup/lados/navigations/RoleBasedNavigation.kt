package org.nullgroup.lados.navigations

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.common.ForgotPasswordScreen
import org.nullgroup.lados.screens.common.LoginScreen
import org.nullgroup.lados.screens.common.RegisterScreen


@Composable
fun RoleBasedNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Common.LoginScreen.route,
    lifecycleScope: LifecycleCoroutineScope
) {

    NavHost(navController = navController, startDestination = startDestination) {
        Screen.Common.getAllScreens().forEach { screen ->
            composable(screen.route) {
                when (screen.route) {
                    Screen.Common.LoginScreen.route -> {
                        LoginScreen(lifecycleScope, navController)
                    }

                    Screen.Common.RegisterScreen.route -> {
                        RegisterScreen(navController, {})
                    }

                    Screen.Common.ForgotPasswordScreen.route -> {
                        ForgotPasswordScreen(navController)
                    }
                }
            }
        }
    }
}