package com.app.xanostoregym.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.xanostoregym.R
import com.app.xanostoregym.api.SessionManager

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saved: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val session = SessionManager(requireContext())

        view.findViewById<TextView>(R.id.tvUserName).text = "Nombre: ${session.fetchUserName()}"
        view.findViewById<TextView>(R.id.tvUserEmail).text = "Email: ${session.fetchUserEmail()}"

        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            session.logout()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        return view
    }
}