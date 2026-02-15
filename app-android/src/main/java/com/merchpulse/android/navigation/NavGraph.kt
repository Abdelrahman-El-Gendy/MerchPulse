package com.merchpulse.android.navigation

sealed class Screen(val route: String) {
    data object SignIn : Screen("signin")
    data object SignUp : Screen("signup")
    data object Home : Screen("home")
    data object Inventory : Screen("inventory")
    data object ProductForm : Screen("product_form/{productId}") {
        fun createRoute(productId: String?) = "product_form/$productId"
    }
    data object LowStock : Screen("low_stock")
    data object Punch : Screen("punch")
    data object Employees : Screen("employees")
    data object TeamPunches : Screen("team_punches")
    data object Settings : Screen("settings")
}
