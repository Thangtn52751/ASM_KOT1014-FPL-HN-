package dev.zero.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.zero.myapplication.screen.*
import dev.zero.myapplication.screens.CartScreen
import dev.zero.myapplication.screens.CheckoutScreen
import dev.zero.myapplication.screens.EditProfileScreen
import dev.zero.myapplication.screens.FavoriteScreen
import dev.zero.myapplication.screens.HomeScreen
import dev.zero.myapplication.screens.OrderHistoryScreen
import dev.zero.myapplication.screens.OrderSuccessScreen
import dev.zero.myapplication.screens.ProductDetailScreen
import dev.zero.myapplication.screens.ProfileScreen
import dev.zero.myapplication.screens.SignInScreen
import dev.zero.myapplication.screens.SignUpScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("intro") { IntroScreen(navController) }
        composable("signin") { SignInScreen(navController) }
        composable("signup") { SignUpScreen(navController) }

        // Tab Screens
        composable("home") { HomeScreen(navController) }
        composable("cart") { CartScreen(navController) }
        composable("favorite") { FavoriteScreen(navController) }
        composable("profile") { ProfileScreen(navController) }

        // Detail screen
        composable("productDetail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(navController, productId)
        }
        composable("checkout/{productIds}") { backStackEntry ->
            val productIdsParam = backStackEntry.arguments?.getString("productIds")
            CheckoutScreen(navController, productIdParam = productIdsParam)
        }


        composable("orderSuccess") {
            OrderSuccessScreen(navController)
        }

        composable("editProfile") {
            EditProfileScreen(navController)
        }

        composable("orderHistory") {
            OrderHistoryScreen(navController)
        }

    }
}
