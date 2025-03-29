package com.savelyev.MobileApp.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.savelyev.MobileApp.R

class ContentActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)

        setupBottomNavigationBar()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        hideToolBars(findViewById(R.id.toolbar))
    }

    private fun setupBottomNavigationBar() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        Log.d("DebugInfo", "Attempting to find NavController")
        try {
            navController = findNavController(R.id.nav_host_fragment)
            Log.d("DebugInfo", "NavController found: $navController")
            bottomNav.setupWithNavController(navController)
            hideNavBars(bottomNav)
        } catch (e: Exception) {
            Log.e("DebugInfo", "Error finding NavController: ${e.message}")
        }
    }


    private fun hideNavBars(bottomNav: BottomNavigationView) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment,
                R.id.loginFragment,
                R.id.registrationFragment-> {
                    bottomNav.visibility = View.GONE
                }

                else -> {
                    bottomNav.visibility = View.VISIBLE
                }
            }
        }

    }
    private fun hideToolBars(toolbar: Toolbar) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment,
                R.id.loginFragment,
                R.id.registrationFragment-> {
                    toolbar.visibility = View.GONE
                }
                else -> {
                    toolbar.visibility = View.VISIBLE
                }
            }
        }

    }
}
