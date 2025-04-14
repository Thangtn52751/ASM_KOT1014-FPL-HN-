package dev.zero.myapplication.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.24.33.25:3000/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val productService: ProductService by lazy {
        retrofit.create(ProductService::class.java)
    }

    val profileService: ProfileService by lazy {
        retrofit.create(ProfileService::class.java)
    }
    val cartService: CartService by lazy {
        retrofit.create(CartService::class.java)
    }

    val favoriteService: FavoriteService by lazy {
        retrofit.create(FavoriteService::class.java)
    }

    val orderService: OrderService by lazy {
        retrofit.create(OrderService::class.java)
    }
}
