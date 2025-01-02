package org.nullgroup.lados.navigations

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import org.nullgroup.lados.screens.staff.ChatScreen
import org.nullgroup.lados.screens.staff.ChatWithCustomerScreen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.customer.profile.ProfileViewModel

private const val TAG = "StaffGraph"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    globalNavHostController: NavHostController,
    startDestination: Screen = Screen.Staff.ChatScreen,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: ProfileViewModel = hiltViewModel()
) {
    var currentDestination by remember {
        mutableStateOf(startDestination)
    }
    var isVisibility by remember {
        mutableStateOf(true)
    }
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column {
                    Text(
                        "Staff panel",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(16.dp)
                    )
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text(text = Screen.Staff.ChatScreen.name!!) },
                        selected = currentDestination.route == Screen.Staff.ChatScreen.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            currentDestination = Screen.Staff.ChatScreen
                            navController.navigate(Screen.Staff.ChatScreen.route)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = Screen.Staff.OrderManagement.name!!) },
                        selected = currentDestination.route == Screen.Staff.OrderManagement.route,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            currentDestination = Screen.Staff.OrderManagement
                            navController.navigate(Screen.Staff.OrderManagement.route)
                        }
                    )
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.signOut(globalNavHostController) }) {
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
                AnimatedVisibility(
                    visible = isVisibility,
                ) {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = LadosTheme.colorScheme.background
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
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination.route
            ) {
                Screen.Staff.getAllScreens().forEach { screen ->
                    composable(screen.route) {
                        when (screen.route) {
                            Screen.Staff.ChatScreen.route -> {
                                isVisibility = true
                                ChatScreen(
                                    modifier = Modifier,
                                    navController = navController,
                                    paddingValues = innerPadding
                                )
                            }

                            Screen.Staff.OrderManagement.route -> {
                                // Reports()
                            }
                        }
                    }
                }

                composable(
                    route = Screen.Staff.ChatWithCustomerScreen.ROUTE_WITH_ARG,
                    arguments = listOf(navArgument(Screen.Staff.ChatWithCustomerScreen.CHAT_ID_ARG) {
                        type = NavType.StringType
                    })
                ) {
                    isVisibility = false
                    ChatWithCustomerScreen(
                        modifier = Modifier,
                        navController = navController,
                        paddingValues = innerPadding,
                    )
                }
            }
        }
    }
}

