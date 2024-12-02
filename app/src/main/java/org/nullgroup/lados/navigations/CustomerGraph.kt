package org.nullgroup.lados.navigations

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.FilterScreenDraw
import org.nullgroup.lados.screens.customer.HomeScreen
import org.nullgroup.lados.screens.customer.MainSearchScreen
import org.nullgroup.lados.screens.customer.ProductScreen

@Composable
fun CustomerGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Customer.HomeScreen.route
) {
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                Screen.Customer.getBaseScreens().forEach { screen ->
                    BottomNavigationItem(
                        icon = { screen.icon?.let { Icon(it, contentDescription = screen.name) } },
                        label = { screen.name?.let { Text(it) } },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(navController = navController, startDestination = startDestination) {

            Screen.Customer.getAllScreens().forEach { screen ->
                composable(screen.route) {
                    when (screen.route) {
                        Screen.Customer.Home.route -> {
                            ProductScreen(navController = navController, paddingValues = innerPadding)
                        }

                        Screen.Customer.SearchScreen.route -> {
                            MainSearchScreen(navController = navController, paddingValues = innerPadding)
                        }

                        Screen.Customer.HomeScreen.route -> {
                            HomeScreen(navController = navController, paddingValues = innerPadding)
                        }

                        Screen.Customer.FilterScreen.route -> {
                            FilterScreenDraw(navController = navController, paddingValues = innerPadding)
                        }

                        Screen.Customer.ChatScreen.route -> {
                            // ChatScreen()
                        }

                        Screen.Customer.Profile.route -> {
//                            ProfileScreen(
//                                modifier = Modifier.padding(
//                                    vertical = 32.dp,
//                                    horizontal = 16.dp
//                                ), paddingValues = innerPadding
//                            )
                        }

                        Screen.Customer.Order.route -> {
                            // Tasks()
                        }

                        Screen.Customer.ChatScreen.route -> {

                        }
                    }
                }
            }
        }
    }
}