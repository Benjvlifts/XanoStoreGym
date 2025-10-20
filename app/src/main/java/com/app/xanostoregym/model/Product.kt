package com.app.xanostoregym.model

/**
 * Data Class que representa la estructura de un producto de la API.
 */
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val brand: String,
    val category: String,
    val stock: Int,
    val image: List<ImageResource> // Un producto puede tener una lista de imágenes
)

/**
 * Data Class para el recurso de imagen anidado en el producto.
 */
data class ImageResource(
    val path: String,
    val name: String,
    val type: String,
    val size: Int,
    val mime: String,
    val url: String // Esta es la URL que usaremos para mostrar la imagen con Glide
)