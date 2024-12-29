package org.nullgroup.lados.navigations

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import org.nullgroup.lados.screens.customer.cart.CartScreen
import org.nullgroup.lados.screens.customer.checkout.CheckoutScreen
import org.nullgroup.lados.screens.common.ForgotPasswordScreen
import org.nullgroup.lados.screens.common.LoginScreen
import org.nullgroup.lados.screens.common.RegisterScreen
import org.nullgroup.lados.screens.customer.Error_FindNotMatchScreen
import org.nullgroup.lados.screens.customer.FilterScreen
import org.nullgroup.lados.screens.customer.HomeScreen
import org.nullgroup.lados.screens.customer.ProductScreen
import org.nullgroup.lados.screens.customer.SearchScreen
import org.nullgroup.lados.screens.customer.order.OrderDetailScreen
import org.nullgroup.lados.screens.customer.order.OrderProductsViewScreen
import org.nullgroup.lados.screens.customer.order.OrderScreen
import org.nullgroup.lados.screens.customer.product.CategorySelectScreen
import org.nullgroup.lados.screens.customer.product.ProductDetailScreen
import org.nullgroup.lados.screens.customer.product.ProductInCategoryScreen
import org.nullgroup.lados.screens.customer.product.ReviewProductScreen
import org.nullgroup.lados.screens.customer.profile.AddAddressScreen
import org.nullgroup.lados.screens.customer.profile.AddressList
import org.nullgroup.lados.screens.customer.profile.EditAddressScreen
import org.nullgroup.lados.screens.customer.profile.EditProfileScreen
import org.nullgroup.lados.screens.customer.profile.ProfileScreen
import org.nullgroup.lados.screens.customer.profile.SettingScreen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.SharedViewModel

    @Composable
fun CustomerGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    sharedViewModel: SharedViewModel = hiltViewModel(),
    startDestination: String = Screen.Customer.Home.route,
    themeSwitched: () -> Unit = {}
) {

    var isVisibility by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier

            .fillMaxWidth()
            .fillMaxHeight(),
        containerColor = LadosTheme.colorScheme.background,
        bottomBar = {
            AnimatedVisibility(
                visible = isVisibility,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                BottomNavigation(
                    backgroundColor = LadosTheme.colorScheme.background,
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    Screen.Customer.getAllScreens().slice(indices = IntRange(0, 3))
                        .forEach { screen ->
                            val isSelected = mutableStateOf(
                                currentDestination?.hierarchy?.any
                                { it.route == screen.route } == true
                            )

                            // note: modify color
                            val contentColor = LadosTheme.colorScheme.primary

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
                                            tint = if (isSelected.value) contentColor else LadosTheme.colorScheme.secondary

                                        )
                                    }
                                },
                                label = {
                                    screen.name?.let {
                                        Text(
                                            text = it,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected.value) contentColor else LadosTheme.colorScheme.secondary
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
                                sharedViewModel = sharedViewModel,
                                modifier = modifier,
                            )
                        }

                        Screen.Customer.Home.route -> {
                            isVisibility = true
                            ProductScreen(
                                navController = navController,
                                paddingValues = innerPadding,
                                sharedViewModel = sharedViewModel,
                                modifier = modifier,
                            )
                        }

                        Screen.Customer.SearchScreen.route -> {
                            SearchScreen(
                                navController = navController,
                                paddingValues = innerPadding,
                                sharedViewModel = sharedViewModel,
                                context = LocalContext.current,
                                modifier = modifier,
                            )
                        }

                        Screen.Customer.HomeScreen.route -> {
                            HomeScreen(
                                navController = navController,
                                paddingValues = innerPadding,
                                sharedViewModel = sharedViewModel,
                                modifier = modifier,
                            )
                        }

                        Screen.Customer.FilterScreen.route -> {
                            FilterScreen(
                                navController = navController,
                                paddingValues = innerPadding,
                                sharedViewModel = sharedViewModel,
                                modifier = modifier,
                            )
                        }

                        Screen.Customer.DisplayProductInCategory.route -> {
                            ProductInCategoryScreen(
                                navController = navController,
                                paddingValues = innerPadding,
                                sharedViewModel = sharedViewModel,
                                context = LocalContext.current,
                                modifier = modifier,
                            )
                        }

                        Screen.Customer.ErrorFindNotMatched.route -> {
                            isVisibility = true
                            Error_FindNotMatchScreen(
                                navController = navController,
                                paddingValues = innerPadding,
                                modifier = modifier,
                            )
                        }

                        Screen.Customer.ChatScreen.route -> {
                            // ChatScreen()
                        }

                        Screen.Customer.Profile.route -> {
                            isVisibility = true
                            ProfileScreen(
                                modifier = modifier,
                                navController = navController,
                                paddingValues = innerPadding
                            )
                        }

                        Screen.Customer.Order.OrderList.route -> {
                            isVisibility = true
                            OrderScreen(
                                modifier = modifier,
                                paddingValues = innerPadding,
                                navController = navController
                            )
                        }
                    }
                }
            }

            composable(
                Screen.Customer.Order.OrderDetail.ROUTE_WITH_ARG,
                arguments = listOf(
                    navArgument(Screen.Customer.Order.OrderDetail.ID_ARG) {
                        type = NavType.StringType
                    })
            ) {
                isVisibility = false
                OrderDetailScreen(
                    modifier = modifier,
                    navController = navController,
                    paddingValues = innerPadding
                )
            }

            composable(
                Screen.Customer.Order.OrderProductsView.ROUTE_WITH_ARG,
                arguments = listOf(
                    navArgument(Screen.Customer.Order.OrderProductsView.ID_ARG) {
                        type = NavType.StringType
                    })
            ) {
                isVisibility = false
                OrderProductsViewScreen(
                    modifier = modifier,
                    navController = navController,
                    paddingValues = innerPadding
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

            composable(route = Screen.Customer.Setting.route) {
                isVisibility = false
                SettingScreen(
                    modifier = modifier,
                    paddingValues = innerPadding,
                    onBack = { navController.navigateUp() },
                    navController = navController,
                    themeSwitched = themeSwitched
                )
            }

            composable(
                route = Screen.Customer.ReviewProductScreen.ROUTE_WITH_ARGS,
                arguments = listOf(
                    navArgument(Screen.Customer.ReviewProductScreen.PRODUCT_ID_ARG) {
                        type = NavType.StringType
                    },

                    navArgument(Screen.Customer.ReviewProductScreen.VARIANT_ID_ARG) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                isVisibility = false

                Log.d("Review:", Screen.Customer.ReviewProductScreen.ROUTE_WITH_ARGS)

                val productId =
                    backStackEntry.arguments?.getString(Screen.Customer.ReviewProductScreen.PRODUCT_ID_ARG)
                val variantId =
                    backStackEntry.arguments?.getString(Screen.Customer.ReviewProductScreen.VARIANT_ID_ARG)

                // Sử dụng productId và variantId trong Composable của bạn
                ReviewProductScreen(
                    productId = productId.toString(),
                    variantId = variantId.toString(),
                    navController = navController,
                    modifier = modifier,
                )
            }

            composable(
                route = Screen.Customer.Address.AddressList.route
            ) {
                isVisibility = false
                AddressList(
                    modifier = modifier,
                    navController = navController,
                    paddingValues = innerPadding
                )
            }

            composable(
                route = Screen.Customer.Address.AddAddress.route
            ) {
                isVisibility = false
                AddAddressScreen(
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
                    navController = navController,
                )
            }

            composable(route = Screen.Customer.CartScreen.route) {
                isVisibility = false
                CartScreen(
                    modifier = Modifier,
                    innerPadding = innerPadding,
                    navController = navController
                )
            }

            composable(route = Screen.Customer.CheckOutScreen.route) {
                isVisibility = false
                CheckoutScreen(
                    modifier = Modifier,
                    innerPadding = innerPadding,
                    navController = navController,
                )
            }

            composable(route = Screen.Common.LoginScreen.route) {
                isVisibility = false
                LoginScreen(modifier = modifier, navController = navController)
            }

            composable(route = Screen.Common.RegisterScreen.route) {
                isVisibility = false
                RegisterScreen(modifier = modifier, navController = navController)
            }

            composable(route = Screen.Common.ForgotPasswordScreen.route) {
                isVisibility = false
                ForgotPasswordScreen(
                    modifier = modifier,
                    navController = navController
                )
            }
        }
    }
}


