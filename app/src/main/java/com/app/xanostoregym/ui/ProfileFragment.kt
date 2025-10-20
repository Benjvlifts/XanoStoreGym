package com.app.xanostoregym.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.app.xanostoregym.R
import com.app.xanostoregym.api.ApiClient
import com.app.xanostoregym.api.SessionManager
import com.app.xanostoregym.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        sessionManager = SessionManager(requireContext())

        // Encontrar las vistas
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)

        btnLogout.setOnClickListener {
            sessionManager.clearAuthToken() // Borramos solo el token

            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Llamamos a la función para cargar los datos del perfil
        loadUserProfile()

        return view
    }

    private fun loadUserProfile() {
        // Obtenemos el token guardado
        val token = sessionManager.fetchAuthToken()

        if (token == null) {
            Toast.makeText(context, "No se encontró token de sesión.", Toast.LENGTH_SHORT).show()
            return
        }

        // Hacemos la llamada a la API en una coroutina
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Hacemos la llamada al nuevo endpoint /auth/me
                val response = ApiClient.instance.getMe("Bearer $token")

                // Cambiamos al hilo principal para actualizar la UI
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            // Actualizamos los TextViews con los datos recibidos
                            updateUI(user)
                        }
                    } else {
                        Toast.makeText(context, "Error al obtener datos del perfil", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ProfileFragment", "Error al cargar perfil", e)
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateUI(user: User) {
        tvUserName.text = "Nombre: ${user.name}"
        tvUserEmail.text = "Email: ${user.email}"
    }
}