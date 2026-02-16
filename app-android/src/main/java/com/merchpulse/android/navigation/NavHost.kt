package com.merchpulse.android.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.merchpulse.feature.employees.ui.EmployeeListScreen
import com.merchpulse.android.ui.SplashScreen
import com.merchpulse.core.common.BiometricUtils
import androidx.compose.ui.platform.LocalContext
import com.merchpulse.feature.auth.ui.SignInScreen
import com.merchpulse.feature.auth.ui.SignUpScreen
import com.merchpulse.feature.home.ui.HomeScreen
import com.merchpulse.feature.home.ui.SettingsScreen
import com.merchpulse.feature.products.ui.ProductListScreen
import com.merchpulse.feature.products.ui.ProductFormScreen
import com.merchpulse.feature.stock.ui.LowStockScreen
import com.merchpulse.feature.punching.ui.PunchScreen
import com.merchpulse.feature.punching.ui.TeamPunchScreen
import com.merchpulse.feature.auth.presentation.SignInViewModel
import org.koin.compose.koinInject

@Composable
fun MerchPulseNavHost(
    navController: NavHostController,
    startDestination: String = Screen.SignIn.route,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp)
) {
    NavHost(
        navController = navController, 
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateNext = { isLoggedIn ->
                    val destination = if (isLoggedIn) Screen.Home.route else Screen.SignIn.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.SignIn.route) {
            val context = LocalContext.current
            val viewModel: SignInViewModel = koinInject()
            SignInScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onBiometricLoginClick = {
                    BiometricUtils.authenticate(context) { success, error ->
                        if (success) {
                            viewModel.onBiometricSuccess()
                        } else {
                            viewModel.onBiometricError(error)
                        }
                    }
                }

            )
        }
        
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateToSignIn = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToInventory = { navController.navigate(Screen.Inventory.route) },
                onNavigateToLowStock = { navController.navigate(Screen.LowStock.route) },
                onNavigateToPunch = { navController.navigate(Screen.Punch.route) },
                onNavigateToEmployees = { navController.navigate(Screen.Employees.route) },
                onNavigateToTeamPunches = { navController.navigate(Screen.TeamPunches.route) }
            )
        }

        composable(Screen.Inventory.route) {
            ProductListScreen(
                onNavigateToDetail = { id -> 
                    navController.navigate(Screen.ProductForm.createRoute(id))
                },
                onNavigateToAdd = {
                    navController.navigate(Screen.ProductForm.createRoute("null"))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ProductForm.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val id = if (productId == "null") null else productId
            ProductFormScreen(
                productId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LowStock.route) {
            LowStockScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Punch.route) {
            PunchScreen(
                onNavigateBack = { navController.popBackStack() },
                scaffoldPadding = padding
            )
        }

        composable(Screen.Employees.route) {
            EmployeeListScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.TeamPunches.route) {
            TeamPunchScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
