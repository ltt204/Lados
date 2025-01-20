package org.nullgroup.lados.navigations

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.admin.coupon.CouponManager
import org.nullgroup.lados.screens.admin.category.AddCategoryScreen
import org.nullgroup.lados.screens.admin.category.CategoryManagementScreen
import org.nullgroup.lados.screens.admin.category.EditCategoryScreen
import org.nullgroup.lados.screens.admin.inventory.InventoryTracking
import org.nullgroup.lados.screens.admin.product.AddEditVariantScreen
import org.nullgroup.lados.screens.admin.product.AddProductScreen
import org.nullgroup.lados.screens.admin.product.AddVariantScreen
import org.nullgroup.lados.screens.admin.product.EditAddVariantScreen
import org.nullgroup.lados.screens.admin.product.EditProductScreen
import org.nullgroup.lados.screens.admin.product.EditVariantScreen
import org.nullgroup.lados.screens.admin.product.ManageProductScreen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.admin.product.AddProductScreenViewModel
import org.nullgroup.lados.viewmodels.admin.product.EditProductScreenViewModel
import org.nullgroup.lados.viewmodels.customer.profile.ProfileViewModel

private const val TAG = "AdminGraph"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    globalNavHostController: NavHostController,
    startDestination: Screen = Screen.Admin.ProductManagement,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: ProfileViewModel = hiltViewModel()
) {
    Log.d(TAG, "AdminGraph: startDestination: ${
        navController.backQueue.map { it.destination.route }
    }")
    var currentDestination by remember {
        mutableStateOf(startDestination)
    }
    var addProductViewModel: AddProductScreenViewModel = hiltViewModel()
    var editProductViewModel: EditProductScreenViewModel = hiltViewModel()

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column {

                    Text(
                        "Admin panel",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(16.dp)
                    )
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text(text = Screen.Admin.Analytics.name!!) },
                        selected = currentDestination.route == Screen.Admin.Analytics.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            currentDestination = Screen.Admin.Analytics
                            navController.navigate(Screen.Admin.Analytics.route)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = Screen.Admin.CategoryManagement.name!!) },
                        selected = currentDestination.route == Screen.Admin.CategoryManagement.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            currentDestination = Screen.Admin.CategoryManagement
                            navController.navigate(Screen.Admin.CategoryManagement.route)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = Screen.Admin.UserManagement.name!!) },
                        selected = currentDestination.route == Screen.Admin.UserManagement.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            currentDestination = Screen.Admin.UserManagement
                            navController.navigate(Screen.Admin.UserManagement.route)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = Screen.Admin.ProductManagement.name!!) },
                        selected = currentDestination.route == Screen.Admin.ProductManagement.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            currentDestination = Screen.Admin.ProductManagement
                            navController.navigate(Screen.Admin.ProductManagement.route)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = Screen.Admin.InventoryTracking.name!!) },
                        selected = currentDestination.route == Screen.Admin.InventoryTracking.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            currentDestination = Screen.Admin.InventoryTracking
                            navController.navigate(Screen.Admin.InventoryTracking.route)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = Screen.Admin.PromotionManagement.name!!) },
                        selected = currentDestination.route == Screen.Admin.PromotionManagement.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            currentDestination = Screen.Admin.PromotionManagement
                            navController.navigate(Screen.Admin.PromotionManagement.route)
                        }
                    )
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.signOut(globalNavHostController)
                    }) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Sign out",
                        color = Color.Red,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }) {
        Scaffold(
            containerColor = LadosTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = LadosTheme.colorScheme.background,
                        scrolledContainerColor = LadosTheme.colorScheme.background,
                        navigationIconContentColor = LadosTheme.colorScheme.onBackground,
                        titleContentColor = LadosTheme.colorScheme.onBackground,
                    ),
                    title = { Text(text = currentDestination.name!!) },
                    navigationIcon = {
                        IconButton(onClick = {
                            Log.d("AdminTopAppBar", "onDrawerClick")
                            scope.launch {
                                Log.d(TAG, "AdminGraph: onDrawerClick: ${drawerState.isClosed}")
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.List,
                                contentDescription = "Back button"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            val padding = innerPadding
            NavHost(navController = navController, startDestination = startDestination.route) {

                composable(route = Screen.Admin.Analytics.route) {
                    // Analytics()
                }

                composable(route = Screen.Admin.UserManagement.route) {
                    // UserManagement()
                }

                composable(route = Screen.Admin.CategoryManagement.route) {
                    CategoryManagementScreen(
                        modifier = Modifier,
                        paddingValues = innerPadding,
                        navController = navController,
                    )
                }

                composable(route = Screen.Admin.AddCategory.route) {
                    AddCategoryScreen(
                        modifier = Modifier,
                        paddingValues = innerPadding,
                        navController = navController
                    )
                }

                composable(
                    route = Screen.Admin.EditCategory.ROUTE_WITH_ARG,
                    arguments = listOf(
                        navArgument(Screen.Admin.EditCategory.ID_ARG) {
                            type = NavType.StringType
                        })
                ) {
                    val categoryId =
                        it.arguments?.getString(Screen.Admin.EditCategory.ID_ARG) ?: ""
                    EditCategoryScreen(
                        modifier = Modifier,
                        categoryId = categoryId,
                        paddingValues = innerPadding,
                        navController = navController
                    )
                }

                composable(route = Screen.Admin.ProductManagement.route) {
                    ManageProductScreen(
                        modifier = Modifier,
                        paddingValues = innerPadding,
                        navController = navController
                    )

                }

                composable(route = Screen.Admin.AddProduct.route) {
                    AddProductScreen(
                        modifier = Modifier,
                        paddingValues = innerPadding,
                        navController = navController,
                        viewModel = addProductViewModel
                    )
                }

                composable(
                    route = Screen.Admin.AddVariant.ROUTE_WITH_ARG,
                    arguments = listOf(
                        navArgument(Screen.Admin.AddVariant.ID_ARG)
                        { type = NavType.StringType }
                    )) {
                    val productId = it.arguments?.getString(Screen.Admin.AddVariant.ID_ARG)

                    AddVariantScreen(
                        modifier = Modifier,
                        productId = productId,
                        navController = navController,
                        paddingValues = innerPadding,
                        viewModel = addProductViewModel
                    )
                }

                composable(
                    route = Screen.Admin.EditAddVariantScreen.ROUTE_WITH_ARG,
                    arguments = listOf(
                        navArgument(Screen.Admin.EditAddVariantScreen.VARIANT_ID_ARG)
                        { type = NavType.StringType }
                    )) {
                    val variantId =
                        it.arguments?.getString(Screen.Admin.EditAddVariantScreen.VARIANT_ID_ARG)

                    if (variantId != null) {
                        EditAddVariantScreen(
                            modifier = Modifier,
                            variantId = variantId.toInt(),
                            navController = navController,
                            paddingValues = innerPadding,
                            viewModel = addProductViewModel
                        )
                    }

                }

                composable(
                    route = Screen.Admin.EditProduct.ROUTE_WITH_ARG,
                    arguments = listOf(
                        navArgument(Screen.Admin.EditProduct.ID_ARG)
                        { type = NavType.StringType }
                    )) {
                    val productId = it.arguments?.getString(Screen.Admin.EditProduct.ID_ARG)
                    if (productId != null) {
                        EditProductScreen(
                            modifier = Modifier,
                            productId = productId,
                            navController = navController,
                            paddingValues = innerPadding,
                            viewModel = editProductViewModel
                        )
                    }
                }

                composable(
                    route = Screen.Admin.EditVariant.ROUTE_WITH_ARG,
                    arguments = listOf(
                        navArgument(Screen.Admin.EditVariant.PRODUCT_ID_ARG)
                        { type = NavType.StringType },
                        navArgument(Screen.Admin.EditVariant.VARIANT_ID_ARG)
                        { type = NavType.StringType }
                    )
                ) {
                    val productId = it.arguments?.getString(Screen.Admin.EditVariant.PRODUCT_ID_ARG)
                    val variantId = it.arguments?.getString(Screen.Admin.EditVariant.VARIANT_ID_ARG)
                    if (productId != null && variantId != null) {
                        EditVariantScreen(
                            modifier = Modifier,
                            productId = productId,
                            variantId = variantId,
                            navController = navController,
                            paddingValues = innerPadding,
                            viewModel = editProductViewModel
                        )
                    }
                }

                composable(route = Screen.Admin.AddEditVariantScreen.ROUTE_WITH_ARG,
                    arguments = listOf(
                        navArgument(Screen.Admin.AddEditVariantScreen.ID_ARG)
                        { type = NavType.StringType }
                    )) {
                    val productId =
                        it.arguments?.getString(Screen.Admin.AddEditVariantScreen.ID_ARG)

                    if (productId != null) {
                        AddEditVariantScreen(
                            modifier = Modifier,
                            paddingValues = innerPadding,
                            viewModel = editProductViewModel,
                            navController = navController,
                            productId = productId
                        )
                    }
                }

                composable(route = Screen.Admin.PromotionManagement.route) {
                    CouponManager(
                        modifier = Modifier,
                        navController = navController,
                        innerPadding = innerPadding,
                    )
                }

                composable(route = Screen.Admin.InventoryTracking.route){
                    InventoryTracking(
                        modifier = Modifier,
                        paddingValues = innerPadding,
                    )
                }
            }
        }
    }
}

