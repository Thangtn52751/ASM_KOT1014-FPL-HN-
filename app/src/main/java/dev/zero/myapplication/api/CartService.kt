package dev.zero.myapplication.api

import dev.zero.myapplication.api.models.CartItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CartService {
    @POST("cart")
    suspend fun addToCart(@Body item: CartItem): Response<CartItem>

    @GET("cart")
    suspend fun getCartByUser(@Query("userId") userId: String): Response<List<CartItem>>

    @PUT("cart/{id}")
    suspend fun updateCartItem(@Path("id") id: String, @Body updatedItem: CartItem): Response<CartItem>
    @DELETE("cart/{id}")
    suspend fun deleteCartItem(@Path("id") id: String): Response<Unit>

}

