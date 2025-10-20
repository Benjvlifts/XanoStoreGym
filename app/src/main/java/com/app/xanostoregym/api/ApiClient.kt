package com.app.xanostoregym.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto Singleton que configura y provee una única instancia de Retrofit para toda la app.
 */
object ApiClient {
    // URL base de tu API de Xano. ¡Esta es la versión corregida!
    private const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:heXMjzUd/"

    val instance: ApiService by lazy {
        // Creamos un interceptor para poder ver las peticiones y respuestas en el Logcat.
        // Es una herramienta fundamental para depurar problemas con la API.
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Le decimos a Retrofit que use Gson
            .build()
        retrofit.create(ApiService::class.java)
    }
}