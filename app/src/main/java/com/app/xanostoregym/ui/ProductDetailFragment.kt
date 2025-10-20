package com.app.xanostoregym.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.app.xanostoregym.R
import com.app.xanostoregym.api.ApiClient
import com.app.xanostoregym.model.Product
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class ProductDetailFragment : Fragment() {

    private var productId: Int = -1
    private lateinit var progressBar: ProgressBar

    // Se usa un companion object con un método newInstance para pasar argumentos al fragmento.
    // Esta es la forma recomendada y segura de hacerlo.
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
        // Recogemos el ID del producto que nos pasaron
        arguments?.let {
            productId = it.getInt(ARG_PRODUCT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progressBarDetail)

        if (productId != -1) {
            fetchProductDetails()
        } else {
            Toast.makeText(context, "ID de producto no válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchProductDetails() {
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.instance.getProductById(productId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { product ->
                            updateUI(product)
                        }
                    } else {
                        Toast.makeText(context, "Error al cargar los detalles", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ProductDetailFragment", "Error al obtener detalles", e)
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun updateUI(product: Product) {
        view?.let {
            val ivImage = it.findViewById<ImageView>(R.id.ivProductImageDetail)
            val tvName = it.findViewById<TextView>(R.id.tvProductNameDetail)
            val tvPrice = it.findViewById<TextView>(R.id.tvProductPriceDetail)
            val tvStock = it.findViewById<TextView>(R.id.tvProductStockDetail)
            val tvDescription = it.findViewById<TextView>(R.id.tvProductDescriptionDetail)

            tvName.text = product.name
            tvDescription.text = product.description
            tvStock.text = "Stock: ${product.stock}"

            val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            format.maximumFractionDigits = 0
            tvPrice.text = format.format(product.price)

            val imageUrl = product.image.firstOrNull()?.url
            Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivImage)
        }
    }
}
