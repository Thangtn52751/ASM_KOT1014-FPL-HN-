package dev.zero.myapplication.api

import dev.zero.myapplication.api.models.Product
import retrofit2.Response
import retrofit2.http.GET

interface ProductService {
    @GET("products")
    suspend fun getProducts(): Response<List<Product>>
}
