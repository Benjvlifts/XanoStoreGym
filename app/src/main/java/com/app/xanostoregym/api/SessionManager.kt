package com.app.xanostoregym.api

import android.content.Context
import android.content.SharedPreferences
import com.app.xanostoregym.model.CartItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("XanoGymPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        const val KEY_TOKEN = "token"
        const val KEY_ROLE = "role"
        const val KEY_USER_ID = "user_id"
        const val KEY_CART = "cart"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
    }

    // --- AUTENTICACIÓN ---
    fun saveAuthToken(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()
    fun fetchAuthToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun clearAuthToken() = prefs.edit().remove(KEY_TOKEN).apply()
    fun logout() = clearAuthToken()

    // --- DATOS USUARIO ---
    fun saveUserRole(role: String) = prefs.edit().putString(KEY_ROLE, role).apply()
    fun fetchUserRole(): String = prefs.getString(KEY_ROLE, "client") ?: "client"

    fun saveUserData(id: Int, name: String, email: String, role: String) {
        prefs.edit()
            .putInt(KEY_USER_ID, id)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putString(KEY_ROLE, role)
            .apply()
    }
    fun fetchUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun fetchUserName(): String = prefs.getString(KEY_NAME, "Usuario") ?: "Usuario"
    fun fetchUserEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    // --- CARRITO ---
    fun getCart(): MutableList<CartItem> {
        val json = prefs.getString(KEY_CART, null) ?: return mutableListOf()
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addToCart(item: CartItem) {
        val cart = getCart()
        val existing = cart.find { it.product.id == item.product.id }
        if (existing != null) {
            // Si ya existe, sumamos la cantidad nueva, pero cuidando el stock
            val newTotal = existing.quantity + item.quantity
            if (newTotal <= item.product.stock) {
                existing.quantity = newTotal
            } else {
                existing.quantity = item.product.stock // Topeamos al stock máximo
            }
        } else {
            cart.add(item)
        }
        saveCart(cart)
    }

    fun updateCartItemQuantity(productId: Int, newQuantity: Int) {
        val cart = getCart()
        val item = cart.find { it.product.id == productId }
        if (item != null) {
            if (newQuantity > 0 && newQuantity <= item.product.stock) {
                item.quantity = newQuantity
                saveCart(cart)
            }
        }
    }

    fun removeFromCart(productId: Int) {
        val cart = getCart()
        cart.removeAll { it.product.id == productId }
        saveCart(cart)
    }

    fun clearCart() = prefs.edit().remove(KEY_CART).apply()

    private fun saveCart(cart: List<CartItem>) {
        prefs.edit().putString(KEY_CART, gson.toJson(cart)).apply()
    }
}