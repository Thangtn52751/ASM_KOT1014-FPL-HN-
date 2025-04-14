package dev.zero.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
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
import java.util.UUID

@Composable
fun ProductDetailScreen(navController: NavController, productId: String) {
    val context = LocalContext.current
    var product by remember { mutableStateOf<Product?>(null) }
    var isFavorite by remember { mutableStateOf(false) }
    var favoriteId by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(productId) {
        coroutineScope.launch {
            try {
                val response = RetrofitInstance.productService.getProducts()
                if (response.isSuccessful) {
                    val allProducts = response.body()
                    product = allProducts?.find { it.id == productId }
                }
                val favs = RetrofitInstance.favoriteService.getFavoritesByUser(loggedInUserId!!)
                if (favs.isSuccessful) {
                    val match = favs.body()?.find { it.productId == productId }
                    if (match != null) {
                        isFavorite = true
                        favoriteId = match.id
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    product?.let { p ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                IconButton(onClick = {
                    coroutineScope.launch {
                        try {
                            if (isFavorite) {
                                val response = RetrofitInstance.favoriteService.removeFromFavorite(favoriteId)
                                if (response.isSuccessful) {
                                    isFavorite = false
                                    favoriteId = ""
                                } else {
                                    Toast.makeText(context, "Failed to remove favorite", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val favItem = FavoriteItem(
                                    id = UUID.randomUUID().toString(),
                                    userId = loggedInUserId ?: return@launch,
                                    productId = p.id
                                )
                                val response = RetrofitInstance.favoriteService.addToFavorite(favItem)
                                if (response.isSuccessful) {
                                    isFavorite = true
                                    favoriteId = favItem.id
                                } else {
                                    Toast.makeText(context, "Failed to add to favorite", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Black
                    )
                }
            }

            AsyncImage(
                model = p.image,
                contentDescription = p.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(p.name.uppercase(), fontWeight = FontWeight.Bold, fontSize = 20.sp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
                }
            }

            Text("Description", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            Text(p.description, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Price ¥${p.price}", color = Color.Red, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val cartItem = CartItem(
                                    id = UUID.randomUUID().toString(),
                                    userId = loggedInUserId ?: return@launch,
                                    productId = p.id,
                                    productName = p.name,
                                    price = p.price,
                                    quantity = 1,
                                    description = p.description,
                                    image = p.image
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
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                ) {
                    Text("Add to cart", color = Color.White)
                }
            }
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}