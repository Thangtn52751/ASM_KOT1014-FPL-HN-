package dev.zero.myapplication.api.models

data class OrderItem(
    val id: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val description: String,
    val image: String
)
