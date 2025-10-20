package com.app.xanostoregym.model

import com.google.gson.annotations.SerializedName

/**
 * Data Class para la respuesta del endpoint de Login.
 * @SerializedName mapea el nombre del JSON ("authToken") al de la propiedad Kotlin ("authToken").
 */
data class LoginResponse(
    @SerializedName("authToken") val authToken: String,
    @SerializedName("user") val user: User?
)

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String
)