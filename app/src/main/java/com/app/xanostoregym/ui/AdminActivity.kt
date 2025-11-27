package com.app.xanostoregym.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.xanostoregym.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val navView = findViewById<BottomNavigationView>(R.id.admin_bottom_navigation)
        navView.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_admin_products -> AdminProductsFragment()
                R.id.nav_admin_users -> AdminUsersFragment()
                R.id.nav_admin_orders -> AdminOrdersFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> AdminProductsFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.admin_fragment_container, fragment).commit()
            true
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.admin_fragment_container, AdminProductsFragment()).commit()
        }
    }
}