package dev.zero.myapplication.api

import dev.zero.myapplication.api.models.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileService {
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<User>

    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>
}
