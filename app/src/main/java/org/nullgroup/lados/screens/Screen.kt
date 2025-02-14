package org.nullgroup.lados.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import org.nullgroup.lados.utilities.UserRole

sealed class Screen(
    val name: String? = null,
    val route: String,
    val icon: ImageVector? = null,
) {
    sealed class Common(
        name: String? = null,
        route: String,
        icon: ImageVector? = null,
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
        icon: ImageVector,
    ) : Screen(name, route, icon) {

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

        data object ProductDetailScreen : Customer(
            "ProductDetailScreen",
            "customer_product_detail_screen",
            Icons.Default.Search
        ) {
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
        data object CheckOutScreen :
            Customer("Check Out", "customer_check_out", Icons.Filled.MailOutline)
        data object WishlistScreen: Customer("Wishlist", "customer_wishlist", Icons.Filled.MailOutline)
        data object CouponScreen: Customer("Coupon", "customer_coupon", Icons.Filled.MailOutline)

        sealed class Address(
            name: String,
            route: String,
            icon: ImageVector,
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
            icon: ImageVector,
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
                    Home,
                    ChatScreen,
                    Order.OrderList,
                    Profile,
                    SearchScreen,
                    FilterScreen,
                    CategorySelectScreen,
                    ErrorFindNotMatched,
                    ProductInCategoryScreen,
                    DisplayProductInCategory,
                    Order.OrderProductsView
                )

            fun getBaseScreens() = listOf(Home, ChatScreen, Order.OrderList, Profile)
        }

    }

    /**
     * Define screens for staff
     */
    sealed class Staff(
        name: String,
        route: String,
        icon: ImageVector,
    ) : Screen(name, route, icon) {
        data object ChatScreen : Staff("Chat", "staff_chat", Icons.Default.MailOutline)
        data object ChatWithCustomerScreen :
            Staff("Chat", "staff_chat", Icons.Default.MailOutline) {
            const val CHAT_ROOM_ID_ARG = "chatId_id"
            const val ROUTE_WITH_ARG = "staff_chat/{$CHAT_ROOM_ID_ARG}"
        }

        data object OrderManagement :
            Staff("Order Management", "staff_order_management", Icons.Default.AccountCircle)


        data object OrderDetail :
            Staff("Order Detail", "staff_order_detail", Icons.Default.AccountCircle) {
            const val ID_ARG = "order_id"
            const val ROUTE_WITH_ARG = "staff_order_detail/{$ID_ARG}"
        }

        data object OrderProducts :
            Staff("Order Products", "staff_order_products", Icons.Default.AccountCircle) {
            const val ID_ARG = "order_id"
            const val ROUTE_WITH_ARG = "staff_order_products/{$ID_ARG}"
        }

        data object SearchScreen : Staff("Search", "staff_search", Icons.Default.MailOutline)

        companion object {
            fun getAllScreens() = listOf(ChatScreen, OrderManagement, SearchScreen)
        }
    }

    /**
     * Define screens for admin
     */
    sealed class Admin(
        name: String,
        route: String,
        icon: ImageVector,
    ) : Screen(name, route, icon) {
        data object UserManagement :
            Admin("User Management", "user_management", Icons.Default.AccountCircle)

        data object UserDetailScreen :
            Admin("User Detail Screen", "user_detail_screen", Icons.Default.AccountCircle)

        data object CategoryManagement:
        Admin("Category Management", "category_management", Icons.Default.AccountCircle)

        data object AddCategory:
        Admin("Add Category", "add_category", Icons.Default.AccountCircle)

        data object EditCategory:
        Admin("Edit Category", "edit_category", Icons.Default.AccountCircle){
            const val ID_ARG = "category_id"
            const val ROUTE_WITH_ARG = "edit_category/{$ID_ARG}"
        }

        data object ProductManagement :
            Admin("Product Management", "product_management", Icons.Default.AccountCircle)

        data object AddProduct :
            Admin("Add Product", "add_product", Icons.Default.AccountCircle)

        data object AddVariant :
            Admin("Add Variant", "add_variant", Icons.Default.AccountCircle) {
            const val ID_ARG = "product_id"
            const val ROUTE_WITH_ARG = "add_variant/{$ID_ARG}"
        }
        data object EditAddVariantScreen :
            Admin("Edit Variant", "edit_add_variant", Icons.Default.AccountCircle){
                const val VARIANT_ID_ARG = "variant_id"
                const val ROUTE_WITH_ARG = "edit_add_variant/{$VARIANT_ID_ARG}"
            }

        data object EditProduct :
            Admin("Edit Product", "edit_product", Icons.Default.AccountCircle) {
            const val ID_ARG = "product_id"
            const val ROUTE_WITH_ARG = "edit_product/{$ID_ARG}"
        }

        data object EditVariant :
            Admin("Edit Variant", "edit_variant", Icons.Default.AccountCircle) {
            const val PRODUCT_ID_ARG = "product_id"
            const val VARIANT_ID_ARG = "variant_id"
            const val ROUTE_WITH_ARG = "edit_variant/{$PRODUCT_ID_ARG}/{$VARIANT_ID_ARG}"
        }

        data object AddEditVariantScreen :
            Admin("Add Edit Variant", "add_edit_variant", Icons.Default.AccountCircle) {
            const val ID_ARG = "product_id"
            const val ROUTE_WITH_ARG = "add_edit_variant/{$ID_ARG}"
        }

        data object PromotionManagement :
            Admin("Promotion Management", "promotion_management", Icons.Default.AccountCircle)

        data object Analytics : Admin("Analytics", "analytics", Icons.Default.AccountCircle)

        data object InventoryTracking:
        Admin("Inventory Tracking", "inventory_tracking", Icons.Default.AccountCircle)

        companion object {
            fun getAllScreens() =
                listOf(Analytics, UserManagement, ProductManagement, PromotionManagement)
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