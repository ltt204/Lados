package org.nullgroup.lados.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import org.nullgroup.lados.data.models.UserRole

sealed class Screen(
    val name: String? = null,
    val route: String,
    val icon: ImageVector? = null
) {
    sealed class Common(
        name: String? = null,
        route: String,
        icon: ImageVector? = null
    ) : Screen(name, route, icon) {
        data object LoginScreen : Common(route = "login")
        data object RegisterScreen : Common(route = "register")

        companion object {
            fun getAllScreens(): List<Common> {
                return listOf(
                    LoginScreen,
                    RegisterScreen
                )
            }
        }
    }

    /**
     * Define screens for customer
     */
    sealed class Customer(
        name: String,
        route: String,
        icon: ImageVector
    ) : Screen(name, route, icon) {
        data object HomeScreen : Customer("Home Screen", "customer_home", Icons.Default.Home)
        data object ChatScreen : Customer("Chat", "customer_chat", Icons.Default.MailOutline)
        data object Order : Customer("Order", "customer_order", Icons.Filled.ShoppingCart)
        data object Profile : Customer("Profile", "customer_profile", Icons.Default.AccountCircle)

        sealed class Address(
            name: String,
            route: String,
            icon: ImageVector
        ) : Customer(name, route, icon) {
            data object AddressList :
                Address("Address List", "customer_address_list", Icons.Default.AccountCircle)

            data object AddAddress :
                Address("Add Address", "customer_add_address", Icons.Default.AccountCircle)

            data object EditAddress : Address(
                "Edit Address",
                "customer_edit_address",
                Icons.Default.AccountCircle
            ) {
                const val ID_ARG = "address_id"
                const val ROUTE_WITH_ARG = "customer_edit_address/{$ID_ARG}"
            }
        }

        companion object {
            fun getAllScreens() = listOf(HomeScreen, ChatScreen, Order, Profile)
        }
    }

    /**
     * Define screens for staff
     */
    sealed class Staff(
        name: String,
        route: String,
        icon: ImageVector
    ) : Screen(name, route, icon) {
        data object ChatScreen : Staff("Chat", "staff_chat", Icons.Default.MailOutline)
        data object Reports : Staff("Reports", "staff_reports", Icons.Default.AccountCircle)
        data object TeamAnalytics :
            Staff("Team Analytics", "staff_team_analytics", Icons.Default.AccountCircle)

        companion object {
            fun getAllScreens() = listOf(ChatScreen, Reports, TeamAnalytics)
        }
    }

    /**
     * Define screens for admin
     */
    sealed class Admin(
        name: String,
        route: String,
        icon: ImageVector
    ) : Screen(name, route, icon) {
        data object AdminPanel : Admin("Panel", "admin_panel", Icons.Default.AccountCircle)
        data object UserManagement :
            Admin("User Management", "user_management", Icons.Default.AccountCircle)

        data object SystemSettings :
            Admin("System Settings", "system_settings", Icons.Default.AccountCircle)

        data object Analytics : Admin("Analytics", "analytics", Icons.Default.AccountCircle)

        companion object {
            fun getAllScreens() = listOf(AdminPanel, UserManagement, SystemSettings, Analytics)
        }
    }

    /**
     * Get screens based on user role
     */
    companion object {
        fun getScreensByRole(role: UserRole): List<Screen> {
            return when (role) {
                UserRole.CUSTOMER -> Common.getAllScreens() + Admin.getAllScreens()
                UserRole.STAFF -> Common.getAllScreens() + Staff.getAllScreens()
                UserRole.ADMIN -> Common.getAllScreens() + Customer.getAllScreens()
            }
        }
    }
}