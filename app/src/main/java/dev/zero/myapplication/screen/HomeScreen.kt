package dev.zero.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import dev.zero.myapplication.api.RetrofitInstance
import dev.zero.myapplication.api.models.CartItem
import dev.zero.myapplication.api.models.Product
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Cake") }
    var searchQuery by remember { mutableStateOf("") }
    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitInstance.productService.getProducts()
                if (response.isSuccessful) {
                    productList = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val filteredProducts = productList.filter {
        it.category.equals(selectedCategory, ignoreCase = true) &&
                it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            val images = listOf(
                "https://www.menudep.com/images/photography/800x500/20200204/145688445-chup-hinh-banh-ngot_08.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQqBVr2jnuBImj_hvXOB2Dj3D4R_solt_zJpw&s",
                "https://naidecor.vn/wp-content/uploads/2020/08/BST-BN-06-scaled.jpg"
            )

            val pagerState = rememberPagerState()

            HorizontalPager(count = images.size, state = pagerState, modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp)) { page ->
                AsyncImage(
                    model = images[page],
                    contentDescription = "Slideshow image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Browse By Category",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CategoryChip("Cake", selectedCategory) { selectedCategory = "Cake" }
                CategoryChip("Donuts", selectedCategory) { selectedCategory = "Donuts" }
                CategoryChip("Cookies", selectedCategory) { selectedCategory = "Cookies" }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts) { product ->
                    ProductCard(product = product, navController = navController)
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Card(
        elevation = 6.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFFF9F9F9),
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clickable {
                navController.navigate("productDetail/${product.id}")
            }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text(
                        product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "\u00A5${product.price}",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val cartItem = CartItem(
                                id = UUID.randomUUID().toString(),
                                userId = loggedInUserId ?: return@launch,
                                productId = product.id,
                                productName = product.name,
                                image = product.image,
                                price = product.price,
                                description = product.description,
                                quantity = 1
                            )

                            val response = RetrofitInstance.cartService.addToCart(cartItem)
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .size(28.dp)
                    .background(Color.Red, shape = RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to cart",
                    tint = Color.White
                )
            }
        }
    }
}


@Composable
fun CategoryChip(category: String, selected: String, onClick: () -> Unit) {
    val isSelected = category == selected
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) Color.Red else Color.LightGray,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(category)
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomBarItem("home", "Home", Icons.Default.Home),
        BottomBarItem("cart", "Cart", Icons.Default.ShoppingCart),
        BottomBarItem("favorite", "Favorite", Icons.Default.Favorite),
        BottomBarItem("profile", "Profile", Icons.Default.Person)
    )
    BottomNavigation(
        backgroundColor = Color(0xFFF4F4F4),
        contentColor = Color.Black
    ) {
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
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
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

data class BottomBarItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)