package com.app.xanostoregym.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.xanostoregym.R
import com.app.xanostoregym.api.ApiClient
import com.app.xanostoregym.api.SessionManager
import com.app.xanostoregym.ui.adapter.UserAdapter
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class AdminUsersFragment : Fragment() {
    private lateinit var rvUsers: RecyclerView
    private lateinit var session: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_generic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        session = SessionManager(requireContext())
        rvUsers = view.findViewById(R.id.recyclerView)
        rvUsers.layoutManager = LinearLayoutManager(context)
        loadUsers()
    }

    private fun loadUsers() {
        val token = session.fetchAuthToken() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = ApiClient.instance.getUsers("Bearer $token")
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful && res.body() != null) {
                        // Aquí pasamos la función lambda que recibe el usuario y el nuevo estado (isChecked)
                        rvUsers.adapter = UserAdapter(res.body()!!) { user, isBlocked ->
                            // Llamamos a la función que ya tenías para actualizar en Xano
                            toggleUserBlock(user.id, isBlocked)
                        }
                    }
                }
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }

    private fun toggleUserBlock(id: Int, block: Boolean) {
        val token = session.fetchAuthToken() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            val json = JSONObject().apply { put("blocked", block) }
            val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
            ApiClient.instance.updateUser("Bearer $token", id, body)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show()
                loadUsers()
            }
        }
    }
}