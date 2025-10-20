package com.app.xanostoregym.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.xanostoregym.R
import com.app.xanostoregym.model.Product
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class ProductAdapter(private var productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(), Filterable {

    private var productListFiltered: List<Product> = productList
    // --- 1. NUEVO: Variable para guardar quién nos va a "escuchar" ---
    private var clickListener: OnItemClickListener? = null

    // --- 2. NUEVO: Una función para que el Fragmento nos diga "Hey, escúchame a mí" ---
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productListFiltered[position]
        holder.bind(product)

        // --- 3. NUEVO: Hacemos que toda la tarjeta del producto sea clickeable ---
        holder.itemView.setOnClickListener {
            // Cuando se haga clic, avisamos al "escuchador" y le pasamos el ID del producto
            clickListener?.onItemClick(product.id)
        }
    }

    override fun getItemCount(): Int = productListFiltered.size

    fun updateData(newProducts: List<Product>) {
        this.productList = newProducts
        this.productListFiltered = newProducts
        notifyDataSetChanged()
    }

    // El ViewHolder se mantiene igual
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)

        fun bind(product: Product) {
            tvProductName.text = product.name
            val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
            format.maximumFractionDigits = 0
            tvProductPrice.text = format.format(product.price)
            val imageUrl = product.image.firstOrNull()?.url
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(ivProductImage)
        }
    }

    // El filtro de búsqueda se mantiene igual
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString()?.lowercase(Locale.getDefault()) ?: ""
                productListFiltered = if (charString.isEmpty()) {
                    productList
                } else {
                    productList.filter { it.name.lowercase(Locale.getDefault()).contains(charString) }
                }
                return FilterResults().apply { values = productListFiltered }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productListFiltered = results?.values as? List<Product> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }

    // --- 4. NUEVO: Definimos el "contrato" de lo que significa "escuchar un clic" ---
    interface OnItemClickListener {
        fun onItemClick(productId: Int)
    }
}