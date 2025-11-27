package com.app.xanostoregym.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.app.xanostoregym.R
import com.app.xanostoregym.api.ApiClient
import com.app.xanostoregym.api.SessionManager
import com.app.xanostoregym.model.CartItem
import com.app.xanostoregym.model.Product
import com.app.xanostoregym.ui.adapter.ImagePagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class ProductDetailFragment : Fragment() {

    private var productId: Int = -1
    private lateinit var progressBar: ProgressBar
    private lateinit var sessionManager: SessionManager

    // Variables Cantidad
    private var currentQuantity = 1
    private var maxStock = 0
    private var currentProduct: Product? = null

    // Vistas
    private lateinit var tvQuantity: TextView
    private lateinit var btnPlus: Button
    private lateinit var btnMinus: Button
    private lateinit var btnAddToCart: Button
    private lateinit var vpCarousel: ViewPager2
    private lateinit var tabLayout: TabLayout

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"
        fun newInstance(productId: Int): ProductDetailFragment {
            val fragment = ProductDetailFragment()
            val args = Bundle()
            args.putInt(ARG_PRODUCT_ID, productId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { productId = it.getInt(ARG_PRODUCT_ID) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)
        sessionManager = SessionManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progressBarDetail)
        vpCarousel = view.findViewById(R.id.vpImageCarousel)
        tabLayout = view.findViewById(R.id.tabLayoutIndicator)

        tvQuantity = view.findViewById(R.id.tvQuantity)
        btnPlus = view.findViewById(R.id.btnPlus)
        btnMinus = view.findViewById(R.id.btnMinus)
        btnAddToCart = view.findViewById(R.id.btnAddToCart)

        // Listeners Cantidad
        btnPlus.setOnClickListener {
            if (currentQuantity < maxStock) {
                currentQuantity++
                updateQuantityDisplay()
            } else {
                Toast.makeText(context, "Stock máximo alcanzado", Toast.LENGTH_SHORT).show()
            }
        }

        btnMinus.setOnClickListener {
            if (currentQuantity > 1) {
                currentQuantity--
                updateQuantityDisplay()
            }
        }

        btnAddToCart.setOnClickListener {
            currentProduct?.let { product ->
                if (maxStock > 0) {
                    sessionManager.addToCart(CartItem(product, currentQuantity))
                    Toast.makeText(context, "Añadido al carrito", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }

        if (productId != -1) fetchProductDetails()
    }

    private fun updateQuantityDisplay() {
        tvQuantity.text = currentQuantity.toString()
    }

    private fun fetchProductDetails() {
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.instance.getProductById(productId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { updateUI(it) }
                    } else {
                        Toast.makeText(context, "Error al cargar detalles", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Detail", "Error", e)
                }
            } finally {
                withContext(Dispatchers.Main) { progressBar.visibility = View.GONE }
            }
        }
    }

    private fun updateUI(product: Product) {
        currentProduct = product
        maxStock = product.stock
        currentQuantity = 1
        updateQuantityDisplay()

        view?.let {
            val tvName = it.findViewById<TextView>(R.id.tvProductNameDetail)
            val tvPrice = it.findViewById<TextView>(R.id.tvProductPriceDetail)
            val tvStock = it.findViewById<TextView>(R.id.tvProductStockDetail)
            val tvDescription = it.findViewById<TextView>(R.id.tvProductDescriptionDetail)

            tvName.text = product.name
            tvDescription.text = product.description
            tvStock.text = "Stock disponible: ${product.stock}"

            if (product.stock == 0) {
                btnAddToCart.isEnabled = false
                btnAddToCart.text = "Agotado"
                currentQuantity = 0
                updateQuantityDisplay()
            }

            val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            format.maximumFractionDigits = 0
            tvPrice.text = format.format(product.price)

            // Configurar Carrusel
            if (product.image.isNotEmpty()) {
                val adapter = ImagePagerAdapter(product.image)
                vpCarousel.adapter = adapter
                // Conectar los puntos (TabLayout) con el ViewPager
                TabLayoutMediator(tabLayout, vpCarousel) { _, _ -> }.attach()
            }
        }
    }
}