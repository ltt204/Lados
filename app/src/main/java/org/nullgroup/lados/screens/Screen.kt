package org.nullgroup.lados.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import org.nullgroup.lados.R
import org.nullgroup.lados.utilities.UserRole

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
        data object ForgotPasswordScreen : Common(route = "forgot_password")

        companion object {
            fun getAllScreens(): List<Common> {
                return listOf(
                    LoginScreen,
                    RegisterScreen,
                    ForgotPasswordScreen,
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

        data object HomeScreen : Customer("Home", "customer_home", Icons.Default.Home)
        data object ChatScreen : Customer("Chat", "customer_chat", Icons.Default.MailOutline)
        data object Profile : Customer("Profile", "customer_profile", Icons.Default.AccountCircle)
        data object Setting : Customer("Setting", "customer_setting", Icons.Default.AccountCircle)


        data object Home : Customer("Home", "customer_home", Icons.Default.Home)
        data object SearchScreen : Customer("Search", "customer_search", Icons.Default.Search)
        data object FilterScreen : Customer("Filter", "customer_filter", Icons.Default.Search)
        data object CategorySelectScreen :
            Customer("Category", "customer_category", Icons.Default.Search)

        data object DisplayProductInCategory : Customer(
            "DisplayProductInCategory",
            "customer_display_product_in_category",
            Icons.Default.Search
        )

        data object ProductInCategoryScreen : Customer(
            "ProductInCategoryScreen",
            "customer_product_in_category_screen",
            Icons.Default.Search
        )

        data object ProductDetailScreen: Customer(
            "ProductDetailScreen",
            "customer_product_detail_screen",
            Icons.Default.Search
        ){
            const val ID_ARG = "product_id"
            const val ROUTE_WITH_ARG = "customer_product_detail_screen/{$ID_ARG}"
        }

        data object ReviewProductScreen : Customer(
            "ReviewProductScreen",
            "review_product_screen",
            Icons.Default.Search
        ) {
            const val PRODUCT_ID_ARG = "product_id"
            const val VARIANT_ID_ARG = "variant_id"
            const val ROUTE_WITH_ARGS = "review_product_screen/{$PRODUCT_ID_ARG}/{$VARIANT_ID_ARG}"
        }

        data object ErrorFindNotMatched :
            Customer(
                "Error_FindNotMatched",
                "customer_error_find_not_matched",
                Icons.Default.Search
            )

        data object EditProfile :
            Customer("Edit Profile", "customer_edit_profile", Icons.Default.AccountCircle)

        data object CartScreen : Customer("Cart", "customer_cart", Icons.Default.ShoppingCart)
        data object CheckOutScreen: Customer("Check Out", "customer_check_out", Icons.Filled.MailOutline)

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

        sealed class Order(
            name: String,
            route: String,
            icon: ImageVector
        ) : Customer(name, route, icon) {
            data object OrderList :
                Order("Order", "customer_order_list", Icons.Default.ShoppingCart)

            data object OrderDetail :
                Order("Order Detail", "customer_order_detail", Icons.Default.AccountCircle) {
                const val ID_ARG = "order_id"
                const val ROUTE_WITH_ARG = "customer_order_detail/{$ID_ARG}"
            }

            data object OrderProductsView :
                Order(
                    "Order Products View",
                    "customer_order_products_view",
                    Icons.Default.AccountCircle
                ) {
                const
                val ID_ARG = "order_id"
                const val ROUTE_WITH_ARG = "customer_order_products_view/{${OrderDetail.ID_ARG}}"
            }
        }

        companion object {
            fun getAllScreens() =
                listOf(
                    HomeScreen,
                    ChatScreen,
                    Order.OrderList,
                    Profile,
                    Home,
                    SearchScreen,
                    FilterScreen,
                    CategorySelectScreen,
                    ErrorFindNotMatched,
                    ProductInCategoryScreen,
                    DisplayProductInCategory,
                    Order.OrderProductsView
                )

            fun getBaseScreens() = listOf(HomeScreen, ChatScreen, Order.OrderList, Profile)
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