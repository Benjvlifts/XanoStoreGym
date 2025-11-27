package com.app.xanostoregym.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// --- AUTH & USER ---
data class LoginResponse(
    @SerializedName("authToken") val authToken: String,
    @SerializedName("user") val user: User?,
    @SerializedName("role") val role: String? = "client"
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("role") val _role: String?, // Recibe nulo si no viene
    @SerializedName("blocked") val _blocked: Boolean? // Recibe nulo si no viene
) : Serializable {
    // Propiedades calculadas seguras
    val role: String
        get() = _role ?: "client" // Si es nulo, usa "client" por defecto

    val blocked: Boolean
        get() = _blocked ?: false // Si es nulo, usa false
}

// --- PRODUCTOS (ACTUALIZADO PARA EL CARRUSEL) ---
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val brand: String,
    val category: String,
    val stock: Int,
    // CAMBIO IMPORTANTE: Ahora definimos explícitamente que es una lista de ImageResource
    val image: List<ImageResource> = emptyList()
) : Serializable {

    // Método seguro para extraer la URL de la primera imagen (para la lista principal)
    fun getFirstImageUrl(): String? {
        return if (image.isNotEmpty()) image[0].url else null
    }
}

// --- NUEVA CLASE: RECURSO DE IMAGEN ---
// Esta es la clase que te faltaba y provocaba el error "Unresolved reference"
data class ImageResource(
    val path: String,
    val name: String,
    val type: String,
    val size: Int,
    val mime: String,
    val url: String // Esta URL es la que usa Glide
) : Serializable

// --- SUBIDA IMAGEN ---
data class ImageUploadResponse(
    val path: String,
    val name: String,
    val type: String,
    val size: Long,
    val mime: String,
    val meta: ImageMeta?
) : Serializable

data class ImageMeta(val width: Int, val height: Int) : Serializable

// --- CARRITO LOCAL ---
data class CartItem(
    val product: Product,
    var quantity: Int
)

// --- PEDIDOS (ORDERS) ---
data class Order(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val total: Double,
    val status: String, // "pending", "shipped", "rejected"
    @SerializedName("created_at") val createdAt: Long,
    val items: Any? = null
)

// Request para crear una orden
data class CreateOrderRequest(
    @SerializedName("user_id") val userId: Int,
    val total: Double,
    val status: String,
    val items: List<CartItemRequest>
)

data class CartItemRequest(
    @SerializedName("product_id") val productId: Int,
    val quantity: Int,
    val price: Double,
    val name: String
)