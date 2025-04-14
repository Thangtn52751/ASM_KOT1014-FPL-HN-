package dev.zero.myapplication.api

import dev.zero.myapplication.api.models.CartItem
import dev.zero.myapplication.api.models.FavoriteItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FavoriteService {
    @GET("favorite")
    suspend fun getFavoritesByUser(@Query("userId") userId: String): Response<List<CartItem>>

    @POST("favorite")
    suspend fun addToFavorite(@Body favoriteItem: FavoriteItem): Response<Unit>

    @DELETE("favorite/{id}")
    suspend fun removeFromFavorite(@Path("id") id: String): Response<Unit>

}
