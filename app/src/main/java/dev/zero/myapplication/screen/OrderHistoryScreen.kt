package dev.zero.myapplication.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dev.zero.myapplication.api.RetrofitInstance
import dev.zero.myapplication.api.models.OrderItem
import kotlinx.coroutines.launch

@Composable
fun OrderHistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var orders by remember { mutableStateOf<List<OrderItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val userId = loggedInUserId ?: return@launch
                val response = RetrofitInstance.orderService.getOrdersByUser(userId)
                if (response.isSuccessful) {
                    orders = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "Failed to load orders", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = Color.White
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(orders) { order ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    AsyncImage(
                        model = order.image,
                        contentDescription = order.productName,
                        modifier = Modifier
                            .size(64.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = order.productName ?: "Product", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = "¥${order.price}", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                Divider()
            }
        }
    }
}
