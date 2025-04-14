package dev.zero.myapplication.api.models

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val description: String,
    val image: String,
    val category: String
)
