package com.app.xanostoregym.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.xanostoregym.R
import com.app.xanostoregym.api.ApiClient
import com.app.xanostoregym.api.SessionManager
import com.app.xanostoregym.ui.adapter.OrderAdapter
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class AdminOrdersFragment : Fragment() {
    private lateinit var rvOrders: RecyclerView
    private lateinit var session: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_generic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        session = SessionManager(requireContext())
        rvOrders = view.findViewById(R.id.recyclerView)
        rvOrders.layoutManager = LinearLayoutManager(context)
        loadOrders()
    }

    private fun loadOrders() {
        val token = session.fetchAuthToken() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = ApiClient.instance.getOrders("Bearer $token")
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful && res.body() != null) {
                        rvOrders.adapter = OrderAdapter(res.body()!!) { order ->
                            showStatusDialog(order.id)
                        }
                    }
                }
            } catch (e: Exception) {}
        }
    }

    private fun showStatusDialog(orderId: Int) {
        val statuses = arrayOf("pending", "shipped", "rejected")
        AlertDialog.Builder(context)
            .setTitle("Actualizar Estado")
            .setItems(statuses) { _, which ->
                updateStatus(orderId, statuses[which])
            }
            .show()
    }

    private fun updateStatus(id: Int, status: String) {
        val token = session.fetchAuthToken() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            val json = JSONObject().apply { put("status", status) }
            val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
            ApiClient.instance.updateOrder("Bearer $token", id, body)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Orden actualizada", Toast.LENGTH_SHORT).show()
                loadOrders()
            }
        }
    }
}