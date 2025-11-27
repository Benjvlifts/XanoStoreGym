package com.app.xanostoregym.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.xanostoregym.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ClientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // --- ERROR ESTABA AQUI ---
        // Al tener app:menu en el XML, esta línea duplicaba los botones.
        // navView.inflateMenu(R.menu.bottom_nav_menu_client) <--- BORRAR ESTO
        // -------------------------

        navView.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_products -> ProductsFragment()
                R.id.nav_cart -> CartFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> ProductsFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
            true
        }

        // Cargar fragmento por defecto si es la primera vez
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProductsFragment()).commit()
        }
    }
}