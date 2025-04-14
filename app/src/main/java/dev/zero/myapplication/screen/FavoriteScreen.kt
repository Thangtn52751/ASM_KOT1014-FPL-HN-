package dev.zero.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import coil.compose.AsyncImage
import dev.zero.myapplication.api.RetrofitInstance
import dev.zero.myapplication.api.models.CartItem
import dev.zero.myapplication.api.models.FavoriteItem
import dev.zero.myapplication.api.models.Product
import kotlinx.coroutines.launch

@Composable
fun FavoriteScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var favoriteItems by remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val favRes = RetrofitInstance.favoriteService.getFavoritesByUser(loggedInUserId ?: return@launch)
                if (favRes.isSuccessful) {
                    val favList = favRes.body() ?: emptyList()
                    val productIds = favList.map { it.productId }
                    val prodRes = RetrofitInstance.productService.getProducts()
                    if (prodRes.isSuccessful) {
                        favoriteItems = prodRes.body()?.filter { it.id in productIds } ?: emptyList()
                    }
                } else {
                    Toast.makeText(context, "Failed to load favorites", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Favorite") }, backgroundColor = Color.White)
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(favoriteItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("productDetail/${item.id}")
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.image,
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, fontWeight = FontWeight.Bold)
                        Text("¥${item.price}", color = Color.Red)
                        Text(item.description, fontSize = 12.sp, maxLines = 1)
                    }

                    IconButton(onClick = {
                        coroutineScope.launch {
                            try {
                                val favRes = RetrofitInstance.favoriteService.getFavoritesByUser(loggedInUserId ?: return@launch)
                                val favItem = favRes.body()?.find { it.productId == item.id }
                                if (favItem != null) {
                                    val res = RetrofitInstance.favoriteService.removeFromFavorite(favItem.id)
                                    if (res.isSuccessful) {
                                        favoriteItems = favoriteItems.filterNot { it.id == item.id }
                                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to remove", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove")
                    }
                }
            }
        }
    }
}