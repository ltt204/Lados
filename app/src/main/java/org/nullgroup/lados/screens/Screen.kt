package org.nullgroup.lados.screens

import org.nullgroup.lados.data.models.UserRole

sealed class Screen(
    val route: String
) {

    /**
     * Define common screens
     */
    sealed class Common(
        route: String
    ) : Screen(route) {
        data object LoginScreen : Common("login")
        data object RegisterScreen : Common("register")

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
        route: String
    ) : Screen(route) {
        data object HomeScreen : Customer("customer/home")
        data object ChatScreen : Customer("customer/chat")
        data object Profile : Customer("profile")
        data object Tasks : Customer("tasks")

        companion object {
            fun getAllScreens() = listOf(HomeScreen, ChatScreen, Profile, Tasks)
        }
    }

    /**
     * Define screens for staff
     */
    sealed class Staff(
        route: String
    ) : Screen(route) {
        data object ChatScreen : Staff("staff_chat")
        data object Reports : Staff("staff_reports")
        data object TeamAnalytics : Staff("staff_analytics")

        companion object {
            fun getAllScreens() = listOf(ChatScreen, Reports, TeamAnalytics)
        }
    }

    /**
     * Define screens for admin
     */
    sealed class Admin(
        route: String
    ) : Screen(route) {
        data object AdminPanel : Admin("admin_panel")
        data object UserManagement : Admin("user_management")
        data object SystemSettings : Admin("system_settings")
        data object Analytics : Admin("analytics")

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