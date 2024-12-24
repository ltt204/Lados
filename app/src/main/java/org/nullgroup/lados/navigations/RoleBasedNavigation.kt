package org.nullgroup.lados.navigations

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.common.ForgotPasswordScreen
import org.nullgroup.lados.screens.common.LoginScreen
import org.nullgroup.lados.screens.common.RegisterScreen
import org.nullgroup.lados.ui.theme.darkColorScheme
import org.nullgroup.lados.ui.theme.lightColorScheme


@Composable
fun RoleBasedNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Common.LoginScreen.route,
    isDarkTheme: Boolean = false,
    themeSwitched: () -> Unit = {}
) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        val colorScheme = if (isDarkTheme) darkColorScheme else lightColorScheme
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    NavHost(modifier = modifier, navController = navController, startDestination = startDestination) {
        Screen.Common.getAllScreens().forEach { screen ->
            composable(screen.route) {
                when (screen.route) {
                    Screen.Common.LoginScreen.route -> {
                        LoginScreen(navController, Modifier, themeSwitched)
                    }

                    Screen.Common.RegisterScreen.route -> {
                        RegisterScreen(navController, Modifier)
                    }

                    Screen.Common.ForgotPasswordScreen.route -> {
                        ForgotPasswordScreen(navController, Modifier)
                    }
                }
            }
        }
    }
}