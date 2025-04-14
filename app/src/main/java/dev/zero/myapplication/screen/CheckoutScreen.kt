package dev.zero.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import dev.zero.myapplication.api.models.OrderItem
import dev.zero.myapplication.api.models.User
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun CheckoutScreen(navController: NavController, productIdParam: String?) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var currentUser by remember { mutableStateOf<User?>(null) }

    // Sửa: split đúng chuỗi productId truyền từ CartScreen
    val productIds: List<String> = productIdParam?.split(";") ?: emptyList()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val userId = loggedInUserId ?: return@launch
                val cartResponse = RetrofitInstance.cartService.getCartByUser(userId)
                if (cartResponse.isSuccessful) {
                    val allCartItems = cartResponse.body() ?: emptyList()
                    // Sửa: so sánh với productId thay vì cartItem.id
                    cartItems = if (productIds.isNotEmpty()) {
                        allCartItems.filter { it.productId in productIds }
                    } else allCartItems
                } else {
                    Toast.makeText(context, "Failed to load cart", Toast.LENGTH_SHORT).show()
                }

                val userResponse = RetrofitInstance.authService.getUserById(userId)
                if (userResponse.isSuccessful) {
                    currentUser = userResponse.body()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val productTotal = cartItems.sumOf { it.price * it.quantity }
    val shippingFee = 950
    val totalAmount = productTotal + shippingFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = Color.White
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("${currentUser?.fullName ?: "Your name"} (${currentUser?.phoneNumber ?: "Phone Number"})", fontWeight = FontWeight.Bold)
            Text(currentUser?.address ?: "Your address will be displayed here", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Selected item", fontWeight = FontWeight.Bold)
            LazyColumn(modifier = Modifier.height(200.dp)) {
                items(cartItems) { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        AsyncImage(
                            model = item.image,
                            contentDescription = item.productName,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.productName ?: "", fontWeight = FontWeight.Bold)
                            Text("¥${item.price}", color = Color.Red)
                            Text(item.description ?: "", fontSize = 12.sp)
                        }
                        IconButton(onClick = {
                            coroutineScope.launch {
                                try {
                                    RetrofitInstance.cartService.deleteCartItem(item.id)
                                    cartItems = cartItems.filterNot { it.id == item.id }
                                    Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Product Amount:", fontWeight = FontWeight.Bold)
            Text("¥$productTotal", color = Color.Red)

            Text("Shipping Fee:", fontWeight = FontWeight.Bold)
            Text("¥$shippingFee", color = Color.Red)

            Text("Total Amount:", fontWeight = FontWeight.Bold)
            Text("¥$totalAmount", color = Color.Red, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Text("Payment Method:", fontWeight = FontWeight.Bold)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Pay with cash")
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Online Banking")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val userId = loggedInUserId ?: return@launch
                            val orders = cartItems.map {
                                OrderItem(
                                    id = UUID.randomUUID().toString(),
                                    userId = userId,
                                    productId = it.productId,
                                    productName = it.productName,
                                    price = it.price,
                                    quantity = it.quantity,
                                    description = it.description,
                                    image = it.image
                                )
                            }
                            orders.forEach {
                                RetrofitInstance.orderService.placeOrder(it)
                            }
                            Toast.makeText(context, "Order placed!", Toast.LENGTH_SHORT).show()
                            navController.navigate("orderSuccess")
                        } catch (e: Exception) {
                            Toast.makeText(context, "Order failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text("Order", color = Color.White)
            }
        }
    }
}
