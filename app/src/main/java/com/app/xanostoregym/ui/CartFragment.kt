package com.app.xanostoregym.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.xanostoregym.R
import com.app.xanostoregym.api.ApiClient
import com.app.xanostoregym.api.SessionManager
import com.app.xanostoregym.model.CartItemRequest
import com.app.xanostoregym.model.CreateOrderRequest
import com.app.xanostoregym.ui.adapter.CartAdapter
import kotlinx.coroutines.*
import java.text.NumberFormat
import java.util.*

class CartFragment : Fragment() {
    private lateinit var session: SessionManager
    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        session = SessionManager(requireContext())
        rvCart = view.findViewById(R.id.rvCart)
        tvTotal = view.findViewById(R.id.tvTotal)
        btnCheckout = view.findViewById(R.id.btnCheckout)
        rvCart.layoutManager = LinearLayoutManager(context)

        loadCart()
        btnCheckout.setOnClickListener { checkout() }
    }

    private fun loadCart() {
        val items = session.getCart()
        val adapter = CartAdapter(
            items,
            onQuantityChange = { item, newQty ->
                session.updateCartItemQuantity(item.product.id, newQty)
                loadCart() // Recargamos para ver precio actualizado
            },
            onDeleteClick = { productId ->
                session.removeFromCart(productId)
                loadCart()
            }
        )
        rvCart.adapter = adapter

        val total = items.sumOf { it.product.price * it.quantity }
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        format.maximumFractionDigits = 0
        tvTotal.text = "Total a Pagar: ${format.format(total)}"

        btnCheckout.isEnabled = items.isNotEmpty()
    }

    private fun checkout() {
        val items = session.getCart()
        if (items.isEmpty()) return

        val token = session.fetchAuthToken() ?: return
        val total = items.sumOf { it.product.price * it.quantity }
        val itemRequests = items.map { CartItemRequest(it.product.id, it.quantity, it.product.price, it.product.name) }

        val order = CreateOrderRequest(session.fetchUserId(), total, "pending", itemRequests)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = ApiClient.instance.createOrder("Bearer $token", order)
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful) {
                        Toast.makeText(context, "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show()
                        session.clearCart()
                        loadCart()
                    } else {
                        Toast.makeText(context, "Error al crear pedido: ${res.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}