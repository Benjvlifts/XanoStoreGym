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

class ProductAdapter(
    private var productList: List<Product>,
    private var onClick: ((Product) -> Unit)? = null
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(), Filterable {

    private var productListFiltered: List<Product> = productList
    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(productId: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productListFiltered[position]
        holder.bind(product)
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
            itemClickListener?.onItemClick(product.id)
        }
    }

    override fun getItemCount(): Int = productListFiltered.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iv: ImageView = itemView.findViewById(R.id.ivProductImage)
        private val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)

        fun bind(product: Product) {
            tvName.text = product.name
            tvPrice.text = "$${product.price.toInt()}"

            val url = product.getFirstImageUrl()
            if (!url.isNullOrEmpty()) {
                Glide.with(itemView).load(url).into(iv)
            } else {
                iv.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                productListFiltered = if (charString.isEmpty()) productList else {
                    productList.filter { it.name.contains(charString, true) }
                }
                return FilterResults().apply { values = productListFiltered }
            }
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productListFiltered = results?.values as List<Product>
                notifyDataSetChanged()
            }
        }
    }
}