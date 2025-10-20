package com.app.xanostoregym.api

import com.app.xanostoregym.model.LoginResponse
import com.app.xanostoregym.model.Product
import com.app.xanostoregym.model.User // Asegúrate de que este import esté
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body loginDetails: RequestBody): Response<LoginResponse>

    @GET("product")
    suspend fun getProducts(): Response<List<Product>>

    @POST("product")
    suspend fun createProduct(@Header("Authorization") token: String, @Body productData: RequestBody): Response<Product>

    @Multipart
    @POST("upload/image")
    suspend fun uploadImage(@Header("Authorization") token: String, @Part image: MultipartBody.Part): Response<List<String>>

    @PATCH("product/{id}")
    suspend fun attachImageToProduct(@Header("Authorization") token: String, @Path("id") productId: Int, @Body imageData: RequestBody): Response<Product>

    // --- NUEVO ENDPOINT ---
    // Endpoint para obtener los datos del usuario autenticado
    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<User>

    @GET("product/{id}")
    suspend fun getProductById(@Path("id") productId: Int): Response<Product>
}