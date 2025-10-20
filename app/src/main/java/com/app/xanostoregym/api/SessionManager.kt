package com.app.xanostoregym.api

import android.content.Context
import android.content.SharedPreferences

/**
 * Clase auxiliar para guardar y recuperar datos de la sesión, como el token del usuario.
 * Utiliza SharedPreferences, el sistema de almacenamiento clave-valor simple de Android.
 */
class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences("XanoStoreGymAppPrefs", Context.MODE_PRIVATE)

    companion object {
        const val AUTH_TOKEN = "auth_token"
    }

    /**
     * Guarda el token de autenticación en SharedPreferences.
     */
    fun saveAuthToken(token: String) {
        prefs.edit().putString(AUTH_TOKEN, token).apply()
    }

    /**
     * Recupera el token de autenticación. Devuelve null si no hay ninguno guardado.
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    /**
     * Borra el token de autenticación para cerrar la sesión.
     */
    fun clearAuthToken() {
        prefs.edit().remove(AUTH_TOKEN).apply()
    }
}