package com.app.xanostoregym.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.xanostoregym.R
import com.app.xanostoregym.api.ApiClient
import com.app.xanostoregym.api.SessionManager
import com.app.xanostoregym.ui.adapter.ProductAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class AdminProductsFragment : Fragment() {
    private lateinit var rvProducts: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var session: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        session = SessionManager(requireContext())
        rvProducts = view.findViewById(R.id.rvAdminProducts)
        fabAdd = view.findViewById(R.id.fabAddProduct)
        rvProducts.layoutManager = GridLayoutManager(context, 2)

        fabAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.admin_fragment_container, AddProductFragment())
                .addToBackStack(null)
                .commit()
        }

        loadProducts()
    }

    private fun loadProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = ApiClient.instance.getProducts()
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful && res.body() != null) {
                        // --- CORRECCIÓN AQUÍ ---
                        // El adaptador nos da un 'product' (Objeto), pero showOptionsDialog quiere un ID (Int).
                        // Solución: Usamos 'product.id'
                        val adapter = ProductAdapter(res.body()!!) { product ->
                            showOptionsDialog(product.id)
                        }

                        // También corregimos la interfaz explícita si se usara
                        adapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
                            override fun onItemClick(productId: Int) {
                                showOptionsDialog(productId)
                            }
                        })

                        rvProducts.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                // Manejo de errores opcional
            }
        }
    }

    private fun showOptionsDialog(productId: Int) {
        AlertDialog.Builder(context)
            .setTitle("Opciones Producto")
            .setItems(arrayOf("Eliminar")) { _, _ -> deleteProduct(productId) }
            .show()
    }

    private fun deleteProduct(id: Int) {
        val token = session.fetchAuthToken() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            // Manejamos la excepción para evitar crasheos si la red falla
            try {
                val res = ApiClient.instance.deleteProduct("Bearer $token", id)
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful) {
                        Toast.makeText(context, "Eliminado", Toast.LENGTH_SHORT).show()
                        loadProducts() // Recargamos la lista
                    } else {
                        Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}