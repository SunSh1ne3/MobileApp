package com.savelyev.MobileApp.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.savelyev.MobileApp.Api.RetrofitClient
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.TokenManager

class ContentActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.initialize(applicationContext)
        setContentView(R.layout.activity_content)
        setupBottomNavigationBar()


        if (TokenManager.getInstance().getToken() == null) {
            Log.e("DebugInfo", "Token null")
        }

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        hideToolBars(findViewById(R.id.toolbar))
    }

    private fun setupBottomNavigationBar() {
        bottomNav = findViewById(R.id.bottom_nav)
        Log.d("NavBar", "Attempting to find NavController")
        try {
            navController = findNavController(R.id.nav_host_fragment)
            Log.d("NavBar", "NavController found: $navController")
            bottomNav.setupWithNavController(navController)
            updateBottomNavigation()
            setListenerBottomNavigation()
            hideNavBars(bottomNav)
        } catch (e: Exception) {
            Log.e("NavBar", "Error finding NavController: ${e.message}")
        }
    }



    private fun updateBottomNavigation() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val selectedItemId = when (destination.id) {
                R.id.listFragment -> R.id.listFragment
                R.id.orderFragment -> R.id.orderFragment
                R.id.profileFragment -> R.id.profileFragment
                else -> -1
            }
            if (selectedItemId != -1 && bottomNav.selectedItemId != selectedItemId) {
                bottomNav.selectedItemId = selectedItemId as Int
            }
        }
    }

    private fun setListenerBottomNavigation() {
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.listFragment -> {
                    navController.navigate(R.id.listFragment)
                    true
                }
                R.id.orderFragment -> {
                    navController.navigate(R.id.orderFragment)
                    true
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
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
