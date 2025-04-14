package dev.zero.myapplication.api.models

data class User(
    val id: String = "",
    val username: String,
    val password: String,
    val email: String,
    val phoneNumber: String,
    val fullName: String,
    val address: String,
    val avatar: String
)
