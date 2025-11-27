package com.app.xanostoregym.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.xanostoregym.R
import com.app.xanostoregym.model.User

// Modificamos el constructor para recibir una función que maneje el cambio del switch
class UserAdapter(
    private val users: List<User>,
    private val onBlockToggle: (User, Boolean) -> Unit // (Usuario, NuevoEstado)
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
        val tvRole: TextView = itemView.findViewById(R.id.tvUserRole)
        val switchBlock: Switch = itemView.findViewById(R.id.switchBlock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // Ahora inflamos nuestro diseño personalizado 'item_user'
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        holder.tvName.text = user.name
        holder.tvEmail.text = user.email
        holder.tvRole.text = "Rol: ${user.role ?: "client"}"

        // Colores según estado (Rojo si está bloqueado)
        if (user.blocked == true) {
            holder.tvName.setTextColor(Color.RED)
        } else {
            holder.tvName.setTextColor(Color.BLACK)
        }

        // IMPORTANTE: Quitamos el listener antes de cambiar el estado para evitar bugs al hacer scroll
        holder.switchBlock.setOnCheckedChangeListener(null)

        // Asignamos el estado actual
        holder.switchBlock.isChecked = user.blocked == true

        // Volvemos a poner el listener
        holder.switchBlock.setOnCheckedChangeListener { _, isChecked ->
            // Llamamos a la función del Fragment para que haga la petición a la API
            onBlockToggle(user, isChecked)

            // Actualizamos visualmente el color (opcional, para feedback inmediato)
            if (isChecked) holder.tvName.setTextColor(Color.RED) else holder.tvName.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount() = users.size
}