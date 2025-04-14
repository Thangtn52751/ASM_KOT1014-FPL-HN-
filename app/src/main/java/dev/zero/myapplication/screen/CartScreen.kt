package dev.zero.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import coil.compose.AsyncImage
import dev.zero.myapplication.api.RetrofitInstance
import dev.zero.myapplication.api.models.CartItem
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun CartScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var selectedItems by remember { mutableStateOf(setOf<String>()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val userId = loggedInUserId ?: return@launch
                val response = RetrofitInstance.cartService.getCartByUser(userId)
                if (response.isSuccessful) {
                    cartItems = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "Failed to load cart", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val totalPrice by derivedStateOf {
        cartItems.filter { selectedItems.contains(it.id) }
            .sumOf { it.price * it.quantity }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            Text(
                text = "Cart",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(cartItems) { item ->
                    CartItemCard(
                        cartItem = item,
                        isSelected = selectedItems.contains(item.id),
                        onToggleSelect = {
                            selectedItems = if (selectedItems.contains(item.id))
                                selectedItems - item.id
                            else
                                selectedItems + item.id
                        },
                        onQuantityChange = { newQty ->
                            coroutineScope.launch {
                                try {
                                    val updated = item.copy(quantity = newQty)
                                    val res = RetrofitInstance.cartService.updateCartItem(updated.id, updated)
                                    if (res.isSuccessful) {
                                        cartItems = cartItems.map {
                                            if (it.id == updated.id) updated else it
                                        }
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onDelete = {
                            coroutineScope.launch {
                                try {
                                    RetrofitInstance.cartService.deleteCartItem(item.id)
                                    cartItems = cartItems.filterNot { it.id == item.id }
                                    selectedItems = selectedItems - item.id
                                    Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error deleting: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Divider()

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectedItems.size == cartItems.size && cartItems.isNotEmpty(),
                        onCheckedChange = { checked ->
                            selectedItems = if (checked) cartItems.map { it.id }.toSet() else emptySet()
                        }
                    )
                    Text("All Product")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${totalPrice}đ", fontWeight = FontWeight.Bold, color = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (selectedItems.isNotEmpty()) {
                                val selectedProductIds = cartItems.filter { selectedItems.contains(it.id) }
                                    .joinToString(";") { it.productId }
                                navController.navigate("checkout/$selectedProductIds")
                            } else {
                                Toast.makeText(context, "No items selected", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                    ) {
                        Text("Check Out", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Checkbox(checked = isSelected, onCheckedChange = { onToggleSelect() })

            AsyncImage(
                model = cartItem.image,
                contentDescription = cartItem.productName,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(cartItem.productName ?: "No name", fontWeight = FontWeight.Bold)
                Text("¥${cartItem.price ?: 0}", color = Color.Red)
                Text(cartItem.description ?: "No description", maxLines = 1, fontSize = 12.sp)
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = { onDelete() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        if (cartItem.quantity > 1) onQuantityChange(cartItem.quantity - 1)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Remove")
                    }
                    Text("${cartItem.quantity}", fontWeight = FontWeight.Bold)
                    IconButton(onClick = {
                        onQuantityChange(cartItem.quantity + 1)
                    }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Add")
                    }
                }
            }
        }
    }
}
