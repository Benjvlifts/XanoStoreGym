package com.app.xanostoregym.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.xanostoregym.R
import com.app.xanostoregym.model.CartItem
import java.text.NumberFormat
import java.util.*

class CartAdapter(
    private val items: List<CartItem>,
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.tvCartName)
        val info: TextView = v.findViewById(R.id.tvCartInfo)
        val quantity: TextView = v.findViewById(R.id.tvCartQuantity)
        val btnPlus: Button = v.findViewById(R.id.btnCartPlus)
        val btnMinus: Button = v.findViewById(R.id.btnCartMinus)
        val btnDelete: ImageButton = v.findViewById(R.id.btnCartDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.name.text = item.product.name

        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        format.maximumFractionDigits = 0
        val totalItemPrice = item.product.price * item.quantity
        holder.info.text = format.format(totalItemPrice)

        holder.quantity.text = item.quantity.toString()

        holder.btnPlus.setOnClickListener {
            // Verificar stock antes de subir
            if (item.quantity < item.product.stock) {
                onQuantityChange(item, item.quantity + 1)
            }
        }

        holder.btnMinus.setOnClickListener {
            if (item.quantity > 1) {
                onQuantityChange(item, item.quantity - 1)
            }
        }

        holder.btnDelete.setOnClickListener { onDeleteClick(item.product.id) }
    }

    override fun getItemCount() = items.size
}