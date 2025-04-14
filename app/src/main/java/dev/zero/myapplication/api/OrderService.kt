package dev.zero.myapplication.api

import dev.zero.myapplication.api.models.OrderItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderService {

    @POST("orders")
    suspend fun placeOrder(@Body orderItem: OrderItem): Response<Unit>

    @GET("orders")
    suspend fun getOrdersByUser(@Query("userId") userId: String): Response<List<OrderItem>>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") orderId: String): Response<OrderItem>
}
