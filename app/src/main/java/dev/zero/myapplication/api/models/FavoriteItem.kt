package dev.zero.myapplication.api.models

data class FavoriteItem(
    val id: String, // ID của favorite (có thể là UUID)
    val userId: String, // ID người dùng
    val productId: String // ID sản phẩm được yêu thích
)