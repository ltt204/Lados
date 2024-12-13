package org.nullgroup.lados.navigations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.profile.AddAddressScreen
import org.nullgroup.lados.screens.customer.profile.AddressList
import org.nullgroup.lados.screens.customer.product.CategorySelectScreen
import org.nullgroup.lados.screens.customer.profile.EditAddressScreen
import org.nullgroup.lados.screens.customer.profile.EditProfileScreen
import org.nullgroup.lados.screens.customer.Error_FindNotMatchScreen
import org.nullgroup.lados.screens.customer.FilterScreen
import org.nullgroup.lados.screens.customer.HomeScreen
import org.nullgroup.lados.screens.customer.product.ProductDetailScreen
import org.nullgroup.lados.screens.customer.product.ProductInCategoryScreen
import org.nullgroup.lados.screens.customer.ProductScreen
import org.nullgroup.lados.screens.customer.SearchScreen
import org.nullgroup.lados.ui.theme.MagentaMaterial
import org.nullgroup.lados.viewmodels.SharedViewModel

@Composable
fun CustomerGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    sharedViewModel: SharedViewModel = hiltViewModel(),
    startDestination: String = Screen.Customer.Home.route,
    paddingValues: PaddingValues = PaddingValues()
) {

    var isVisibility by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier.fillMaxWidth(),
        bottomBar = {
            AnimatedVisibility(
                visible = isVisibility,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                BottomNavigation(
                    backgroundColor = Color.White,
                ) {


                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    Screen.Customer.getAllScreens().slice(indices = IntRange(0, 3))
                        .forEach { screen ->

                            val isSelected = mutableStateOf(
                                currentDestination?.hierarchy?.any
                                { it.route == screen.route } == true
                            )

                            val contentColor = MagentaMaterial

                            BottomNavigationItem(
                                modifier = Modifier
                                    .background(Color.Transparent)
                                    .padding(top = 10.dp, bottom = 15.dp),
                                icon = {
                                    screen.icon?.let {
                                        Icon(
                                            imageVector = it,
                                            contentDescription = screen.name,
                                            modifier = Modifier
                                                .size(30.dp)
                                                .padding(bottom = 5.dp),
                                            tint = if (isSelected.value) contentColor else Color.Gray

                                        )
                                    }
                                },
                                label = {
                                    screen.name?.let {
                                        Text(
                                            text = it,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected.value) contentColor else Color.Gray
                                        )
                                    }
                                },
                                selectedContentColor = MaterialTheme.colorScheme.primary,
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

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {

            Screen.Customer.getAllScreens().forEach { screen ->
                composable(screen.route) {
                    when (screen.route) {
                        Screen.Customer.CategorySelectScreen.route -> {
                            CategorySelectScreen(
                                navController = navController,
                                paddingValues = innerPadding,
                                sharedViewModel = sharedViewModel
                            )
                        }

                        Screen.Customer.Home.route -> {
                            isVisibility = true
                            ProductScreen(
                                navController = navController,
                                paddingValues = innerPadding,
                                sharedViewModel = sharedViewModel
                            )
                        }

                        Screen.Customer.SearchScreen.route -> {
                            SearchScreen(
                                navController = navController,
                                paddingValues = innerPadding,
                                sharedViewModel = sharedViewModel
                            )
                        }

                        Screen.Customer.HomeScreen.route -> {
                            HomeScreen(
                                navController = navController,
                                paddingValues = innerPadding, sharedViewModel = sharedViewModel
                            )
                        }

                        Screen.Customer.FilterScreen.route -> {
                            FilterScreen(
                                navController = navController,
                                paddingValues = innerPadding,
                                sharedViewModel = sharedViewModel
                            )
                        }

                        Screen.Customer.DisplayProductInCategory.route -> {
                            ProductInCategoryScreen(
                                navController = navController,
                                paddingValues = innerPadding,
                                sharedViewModel = sharedViewModel
                            )
                        }

                        Screen.Customer.ErrorFindNotMatched.route -> {
                            Error_FindNotMatchScreen(
                                navController = navController,
                                paddingValues = innerPadding
                            )
                        }

                        Screen.Customer.ChatScreen.route -> {
                            // ChatScreen()
                        }

                        Screen.Customer.Profile.route -> {
                            isVisibility = false
                            AddressList(
                                modifier = Modifier,
                                navController = navController,
                                paddingValues = innerPadding
                            )
                        }

                        Screen.Customer.Order.route -> {
                            // Tasks()
                        }

                        Screen.Customer.Address.AddressList.route -> {
                            isVisibility = false
                            AddressList(
                                modifier = Modifier,
                                navController = navController,
                                paddingValues = innerPadding
                            )
                        }

                        Screen.Customer.Address.AddAddress.route -> {
                            isVisibility = false
                            AddAddressScreen(
                                modifier = Modifier,
                                paddingValues = innerPadding,
                                navController = navController
                            )
                        }

                        Screen.Customer.EditProfile.route -> {
                            isVisibility = false
                            EditProfileScreen(
                                modifier = Modifier,
                                paddingValues = innerPadding,
                                navController = navController
                            )
                        }
                    }
                }
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
                    modifier = Modifier,
                    navController = navController,
                    paddingValues = innerPadding
                )
            }

            composable(
                route = Screen.Customer.ProductDetailScreen.ROUTE_WITH_ARG,
                arguments = listOf(
                    navArgument(Screen.Customer.ProductDetailScreen.ID_ARG) {
                        type = NavType.StringType
                    })
            ) { backStackEntry ->
                isVisibility = false
                val productId =
                    backStackEntry.arguments?.getString(Screen.Customer.ProductDetailScreen.ID_ARG)
                        ?: ""
                ProductDetailScreen(
                    productId = productId,
                    onAddToBag = {},
                    navController = navController,
                )
            }
        }
    }
}


