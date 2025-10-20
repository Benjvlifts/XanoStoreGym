package com.app.xanostoregym.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.xanostoregym.R
import com.app.xanostoregym.api.ApiClient
import com.app.xanostoregym.api.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        // Si ya hay un token, vamos directo a la pantalla principal
        if (sessionManager.fetchAuthToken() != null) {
            navigateToMain()
            return
        }

        setContentView(R.layout.activity_login)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonObject = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                }
                val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val response = ApiClient.instance.login(requestBody)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        // Esta comprobación ahora sí será verdadera
                        if (loginResponse != null) {
                            sessionManager.saveAuthToken(loginResponse.authToken)

                            // 👇 USA UN SAFE CALL "?." PARA ACCEDER AL NOMBRE DE FORMA SEGURA
                            // Si el usuario es nulo, usará "Usuario" como nombre por defecto.
                            val userName = loginResponse.user?.name ?: "Usuario"

                            Toast.makeText(this@LoginActivity, "¡Bienvenido, $userName!", Toast.LENGTH_LONG).show()
                            navigateToMain()
                        } else {
                            // Este bloque ya no se ejecutará si el login es exitoso
                            Toast.makeText(this@LoginActivity, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("LoginActivity", "Error en la llamada de login", e)
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.GONE
                }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Cierra esta actividad para que el usuario no pueda volver
    }
}