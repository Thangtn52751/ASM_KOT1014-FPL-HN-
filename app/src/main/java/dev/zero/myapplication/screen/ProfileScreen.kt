package dev.zero.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import dev.zero.myapplication.api.RetrofitInstance
import dev.zero.myapplication.api.models.User
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    var user by remember { mutableStateOf<User?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val id = dev.zero.myapplication.screens.loggedInUserId
                if (!id.isNullOrEmpty()) {
                    val response = RetrofitInstance.profileService.getUserById(id)
                    if (response.isSuccessful) {
                        user = response.body()
                    } else {
                        Toast.makeText(context, "Failed to load user", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(0xFFF4F4F4),
                contentColor = Color.Black
            ) {
                val items = listOf(
                    BottomBarItem("home", "Home", Icons.Default.Home),
                    BottomBarItem("cart", "Cart", Icons.Default.ShoppingCart),
                    BottomBarItem("favorite", "Favorite", Icons.Default.Favorite),
                    BottomBarItem("profile", "Profile", Icons.Default.Person)
                )
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        selectedContentColor = Color.Red,
                        unselectedContentColor = Color.Gray
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .padding(16.dp)
            ) {
                Text(
                    text = "My Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    AsyncImage(
                        model = user?.avatar ?: "https://cdn-icons-png.flaticon.com/512/147/147144.png",
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(user?.fullName ?: "Guest", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(user?.email ?: "guest@example.com", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileOption(icon = Icons.Default.Person, title = "Edit Profile") {
                navController.navigate("editProfile")
            }
            ProfileOption(icon = Icons.Outlined.Refresh, title = "Order History") {
                navController.navigate("orderHistory")
            }
            ProfileOption(icon = Icons.Default.ExitToApp, title = "Log Out") {
                dev.zero.myapplication.screens.loggedInUserId = null
                navController.navigate("signin") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }
    }
}

@Composable
fun ProfileOption(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 15.sp)
    }
    Divider()
}
