package com.app.xanostoregym.api

import com.app.xanostoregym.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- AUTH ---
    @POST("auth/login")
    suspend fun login(@Body loginDetails: RequestBody): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<User>

    // --- PRODUCTOS ---
    @GET("product")
    suspend fun getProducts(): Response<List<Product>>

    @GET("product/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<Product>

    @POST("product")
    suspend fun createProduct(@Header("Authorization") token: String, @Body data: RequestBody): Response<Product>

    @DELETE("product/{id}")
    suspend fun deleteProduct(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("upload/image")
    suspend fun uploadImage(@Header("Authorization") token: String, @Part image: MultipartBody.Part): Response<List<ImageUploadResponse>>

    @PATCH("product/{id}")
    suspend fun attachImageToProduct(@Header("Authorization") token: String, @Path("id") id: Int, @Body data: RequestBody): Response<Product>

    // --- USUARIOS (ADMIN) ---
    @GET("user")
    suspend fun getUsers(@Header("Authorization") token: String): Response<List<User>>

    @PATCH("user/{id}")
    suspend fun updateUser(@Header("Authorization") token: String, @Path("id") id: Int, @Body data: RequestBody): Response<User>

    // --- ORDENES ---
    @POST("order")
    suspend fun createOrder(@Header("Authorization") token: String, @Body data: CreateOrderRequest): Response<Order>

    @GET("order")
    suspend fun getOrders(@Header("Authorization") token: String): Response<List<Order>>

    @PATCH("order/{id}")
    suspend fun updateOrder(@Header("Authorization") token: String, @Path("id") id: Int, @Body data: RequestBody): Response<Order>
}