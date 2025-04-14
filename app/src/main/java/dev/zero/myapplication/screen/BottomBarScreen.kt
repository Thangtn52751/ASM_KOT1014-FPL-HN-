package dev.zero.myapplication.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomBarScreen("home", "Home", Icons.Default.Home)
    object Cart : BottomBarScreen("cart", "Cart", Icons.Default.ShoppingCart)
    object Favorite : BottomBarScreen("favorite", "Favorite", Icons.Default.Favorite)
    object Profile : BottomBarScreen("profile", "Profile", Icons.Default.Person)
}
