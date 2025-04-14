package dev.zero.myapplication.api


import dev.zero.myapplication.api.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthService {
    @GET("users")
    suspend fun loginUser(
        @retrofit2.http.Query("username") username: String,
        @retrofit2.http.Query("password") password: String
    ): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<User>

    @POST("users")
    suspend fun registerUser(@Body user: User): Response<User>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: User): Response<User>


}
