package com.app.xanostoregym.model

/**
 * Data Class para el objeto anidado 'meta' que contiene las dimensiones de la imagen.
 */
data class ImageMeta(
    val width: Int,
    val height: Int
)

/**
 * Data Class para representar la respuesta COMPLETA del endpoint de subida de imagen.
 * Ahora incluye el campo 'meta' que Xano requiere.
 */
data class ImageUploadResponse(
    val path: String,
    val name: String,
    val type: String,
    val size: Long, // Cambiado a Long para tamaños de archivo grandes
    val mime: String,
    val meta: ImageMeta // El campo que faltaba
)