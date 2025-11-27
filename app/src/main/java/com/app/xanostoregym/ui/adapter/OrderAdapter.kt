package com.app.xanostoregym.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.xanostoregym.model.Order

class OrderAdapter(private val orders: List<Order>, val onClick: (Order) -> Unit) :
    RecyclerView.Adapter<OrderAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val t1: TextView = v.findViewById(android.R.id.text1)
        val t2: TextView = v.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val o = orders[position]
        holder.t1.text = "Orden #${o.id} - Total: $${o.total}"
        holder.t2.text = "Estado: ${o.status} - UserID: ${o.userId}"
        holder.itemView.setOnClickListener { onClick(o) }
    }

    override fun getItemCount() = orders.size
}