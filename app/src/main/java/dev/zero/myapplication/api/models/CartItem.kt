package dev.zero.myapplication.api.models
data class CartItem(
    val id: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val image: String,
    val price: Double,
    val description: String,
    val quantity: Int
)
