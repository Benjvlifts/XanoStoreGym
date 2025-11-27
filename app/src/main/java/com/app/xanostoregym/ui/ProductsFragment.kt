package com.app.xanostoregym.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.xanostoregym.R
import com.app.xanostoregym.api.ApiClient
import com.app.xanostoregym.api.SessionManager
import com.app.xanostoregym.model.CartItem
import com.app.xanostoregym.ui.adapter.ProductAdapter
import kotlinx.coroutines.*

class ProductsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar // 1. Declaramos la variable
    private lateinit var session: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager(requireContext())

        // 2. Encontramos las vistas (incluida la progressBar)
        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        searchView = view.findViewById(R.id.searchView)
        progressBar = view.findViewById(R.id.progressBar)

        recyclerView.layoutManager = GridLayoutManager(context, 2)

        loadProducts()

        // Configuración del buscador
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                (recyclerView.adapter as? ProductAdapter)?.filter?.filter(newText)
                return true
            }
        })
    }

    private fun loadProducts() {
        progressBar.visibility = View.VISIBLE // Ahora esto funcionará

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = ApiClient.instance.getProducts()
                withContext(Dispatchers.Main) {
                    if (res.isSuccessful && res.body() != null) {
                        // Configuración del adaptador con clic para ir a detalles
                        val adapter = ProductAdapter(res.body()!!) { product ->
                            // Navegación al detalle del producto
                            val detailFragment = ProductDetailFragment.newInstance(product.id)
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, detailFragment)
                                .addToBackStack(null)
                                .commit()
                        }
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(context, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ProductsFragment", "Error de red", e)
                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    // Ocultamos la barra de progreso al terminar (éxito o error)
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
} // 3. Esta es la llave de cierre que te faltaba