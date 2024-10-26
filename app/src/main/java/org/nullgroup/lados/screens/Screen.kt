package org.nullgroup.lados.screens

sealed class Screen(
    val screen: String,
    val route: String
) {
    sealed class Customer(
        screen: String,
        route: String
    ) : Screen(screen, route) {
        data object HomeScreen : Admin("HomeScreen", "home")
        data object ChatScreen : Customer("ChatScreen", "chat")
    }

    sealed class Staff(
        screen: String,
        route: String
    ) : Screen(screen, route) {
        data object Dashboard : Staff("Dashboard", "dashboard")
        data object ChatScreen : Staff("ChatScreen", "chat")
    }

    sealed class Admin(
        screen: String,
        route: String
    ) : Screen(screen, route) {
        data object Dashboard : Admin("Dashboard", "dashboard")
        data object HomeScreen : Admin("HomeScreen", "home")
    }
}