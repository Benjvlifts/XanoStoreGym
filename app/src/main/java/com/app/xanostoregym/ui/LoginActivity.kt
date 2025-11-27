package com.app.xanostoregym.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.xanostoregym.api.ApiClient
import com.app.xanostoregym.api.SessionManager
import com.app.xanostoregym.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // 1. Verificar si ya existe una sesión guardada al abrir la app
        if (sessionManager.fetchAuthToken() != null) {
            // Si ya hay token, redirigimos según el rol guardado
            navigateByRole(sessionManager.fetchUserRole())
            return
        }

        // 2. Configurar el botón de Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                performLogin(email, pass)
            } else {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performLogin(email: String, pass: String) {
        // Mostrar cargando
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // A) Preparar el JSON para el login
                val json = JSONObject().apply {
                    put("email", email)
                    put("password", pass)
                }
                val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

                // B) Llamada al endpoint /auth/login
                val loginRes = ApiClient.instance.login(body)

                if (loginRes.isSuccessful && loginRes.body() != null) {
                    val token = loginRes.body()!!.authToken

                    // C) ¡IMPORTANTE! Llamar a /auth/me para verificar bloqueo y rol real
                    // (El login a veces no devuelve todos los datos actualizados, mejor verificar siempre)
                    val meRes = ApiClient.instance.getMe("Bearer $token")

                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true

                        if (meRes.isSuccessful && meRes.body() != null) {
                            val user = meRes.body()!!

                            // --- D) VALIDACIÓN DE BLOQUEO ---
                            if (user.blocked == true) {
                                // 🛑 EL USUARIO ESTÁ BLOQUEADO
                                Toast.makeText(this@LoginActivity, "TU CUENTA HA SIDO BLOQUEADA POR EL ADMINISTRADOR", Toast.LENGTH_LONG).show()
                                sessionManager.clearAuthToken() // Aseguramos que no se guarde nada
                            } else {
                                // ✅ EL USUARIO ESTÁ ACTIVO
                                sessionManager.saveAuthToken(token)

                                // Guardamos el rol (si viene nulo, asumimos cliente)
                                val role = user.role ?: "client"
                                sessionManager.saveUserRole(role)

                                Toast.makeText(this@LoginActivity, "Bienvenido ${user.name}", Toast.LENGTH_SHORT).show()
                                navigateByRole(role)
                            }
                            // --------------------------------

                        } else {
                            Toast.makeText(this@LoginActivity, "Error al obtener perfil de usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        Toast.makeText(this@LoginActivity, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Log.e("LoginError", "Error: ${e.message}")
                    Toast.makeText(this@LoginActivity, "Error de conexión: Verifique su internet", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateByRole(role: String) {
        val intent = if (role == "admin") {
            // Si es admin, ir a la pantalla de Admin
            Intent(this, AdminActivity::class.java)
        } else {
            // Si es cualquier otra cosa (cliente), ir a la pantalla de Cliente
            Intent(this, ClientActivity::class.java) // Ojo: En tu código anterior era MainActivity o ClientActivity, usa el que corresponda
        }
        // Flags para limpiar el historial y que no puedan volver al login con "atrás"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}