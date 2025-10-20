package com.app.xanostoregym.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.xanostoregym.R
import com.app.xanostoregym.api.ApiClient
import com.app.xanostoregym.ui.adapter.ProductAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// --- 1. NUEVO: Le decimos al Fragment que "sabe cómo escuchar" los clics del adapter ---
class ProductsFragment : Fragment(), ProductAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        progressBar = view.findViewById(R.id.progressBar)
        searchView = view.findViewById(R.id.searchView)
        setupRecyclerView()
        setupSearchView()
        fetchProducts()
        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        adapter = ProductAdapter(emptyList())
        // --- 2. NUEVO: Le pasamos este fragmento (this) al adapter como el "escuchador" oficial ---
        adapter.setOnItemClickListener(this)
        recyclerView.adapter = adapter
    }

    // Las funciones fetchProducts y setupSearchView se mantienen igual
    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    private fun fetchProducts() {
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.instance.getProducts()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { adapter.updateData(it) }
                    } else {
                        Toast.makeText(context, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ProductsFragment", "Error al obtener productos", e)
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    // --- 3. NUEVO: Esta es la función que se ejecuta cuando el adapter nos avisa de un clic ---
    override fun onItemClick(productId: Int) {
        // Creamos una instancia del fragmento de detalle, pasándole el ID del producto que se clickeó
        val detailFragment = ProductDetailFragment.newInstance(productId)

        // Realizamos la transacción para reemplazar el fragmento actual por el de detalle
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, detailFragment)
            // ¡MUY IMPORTANTE! Añadimos la transacción a la "pila de atrás".
            // Esto permite que el usuario pueda volver a la lista con el botón "atrás" del celular.
            addToBackStack(null)
            commit()
        }
    }
}