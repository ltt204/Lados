package org.nullgroup.lados.navigations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.common.LoginScreen
import org.nullgroup.lados.screens.customer.AddAddressScreen
import org.nullgroup.lados.screens.customer.AddressList
import org.nullgroup.lados.screens.customer.EditAddressScreen
import org.nullgroup.lados.screens.customer.EditProfileScreen
import org.nullgroup.lados.screens.customer.HomeScreen
import org.nullgroup.lados.screens.customer.ProfileScreen

@Composable
fun CustomerGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Customer.HomeScreen.route,
) {
    var isVisibility by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            AnimatedVisibility(
                visible = isVisibility,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                BottomNavigation {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    Screen.Customer.getAllScreens().forEach { screen ->
                        BottomNavigationItem(
                            icon = {
                                screen.icon?.let {
                                    Icon(
                                        it,
                                        contentDescription = screen.name
                                    )
                                }
                            },
                            label = { screen.name?.let { Text(it) } },
                            alwaysShowLabel = false,
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
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = startDestination) {
            composable(route = Screen.Customer.HomeScreen.route) {
                isVisibility = true
                HomeScreen(
                    navController = navController,
                    paddingValues = innerPadding,
                    modifier = modifier
                )
            }

            composable(route = Screen.Customer.ChatScreen.route) {
                // ChatScreen()
            }

            composable(route = Screen.Customer.Profile.route) {
                isVisibility = true
                ProfileScreen(
                    modifier = modifier,
                    paddingValues = innerPadding,
                    navController = navController
                )
            }

            composable(route = Screen.Customer.Address.AddressList.route) {
                isVisibility = false
                AddressList(
                    modifier = modifier,
                    navController = navController,
                    paddingValues = innerPadding
                )
            }

            composable(
                route = Screen.Customer.Address.EditAddress.ROUTE_WITH_ARG,
                arguments = listOf(
                    navArgument(Screen.Customer.Address.EditAddress.ID_ARG) {
                        type = NavType.StringType
                    })
            ) {
                isVisibility = false
                EditAddressScreen(
                    modifier = modifier,
                    navController = navController,
                    paddingValues = innerPadding
                )
            }

            composable(route = Screen.Customer.Address.AddAddress.route) {
                isVisibility = false
                AddAddressScreen(
                    modifier = modifier,
                    paddingValues = innerPadding,
                    navController = navController
                )
            }

            composable(route = Screen.Customer.EditProfile.route) {
                isVisibility = false
                EditProfileScreen(
                    modifier = modifier,
                    paddingValues = innerPadding,
                    navController = navController
                )
            }

            composable(route = Screen.Common.LoginScreen.route) {
                isVisibility = false
                LoginScreen(modifier = modifier, navController = navController)
            }
        }
    }
}
