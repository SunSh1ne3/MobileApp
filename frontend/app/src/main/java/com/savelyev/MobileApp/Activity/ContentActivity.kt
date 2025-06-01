package com.savelyev.MobileApp.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.savelyev.MobileApp.Api.RetrofitClient
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.PushManager
import com.savelyev.MobileApp.Utils.TokenManager
import com.savelyev.MobileApp.Utils.UserManager

class ContentActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private var addButton: ImageView? = null
    private var scanQRButton: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.initialize(applicationContext)
        setContentView(R.layout.activity_content)
        UserManager.init(applicationContext)
        PushManager.init(applicationContext)
        toolbar = findViewById(R.id.toolbar)
        bottomNav = findViewById(R.id.bottom_nav)

        if (TokenManager.getInstance().getToken() == null) {
            Log.e("DebugInfo", "Token null")
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupNavigation()

        //TODO: от предков: при 401
        if (intent?.getStringExtra("SHOW_FRAGMENT") == "LOGIN") {
            navController.navigate(R.id.loginFragment)
        }
    }

    private fun setupNavigation() {
        navController = findNavController(R.id.nav_host_fragment)

        // Конфигурация для фрагментов без кнопки "Назад"
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.listFragment, R.id.orderFragment, R.id.profileFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)

        // Обновление видимости элементов
        updateBottomNavigation()
        setListenerBottomNavigation()

        // Обновление заголовка
        navController.addOnDestinationChangedListener { _, destination, args ->
            when (destination.id) {
                R.id.cardElementFragment -> {
                    val bikeName = args?.getString("bikeName")
                    updateToolbarTitle(bikeName ?: "Велосипед", destination.id)
                }
                else -> {
                    updateToolbarTitle(destination.label?.toString(), destination.id)
                }
            }
            updateNavigationVisibility(destination.id)
        }
    }

    private fun updateToolbarTitle(title: String?, destinationId: Int) {
        val cleanTitle = title?.trim()?.replace("\\s+".toRegex(), " ") ?: ""
        toolbar.findViewById<TextView>(R.id.toolbar_title)?.text = cleanTitle

        val isAdminOrManager = UserManager.isAdminOrManager()

        addButton = findViewById(R.id.add_bicycle_button)
        scanQRButton = findViewById(R.id.scan_qr_button)

        when (isAdminOrManager) {
            true -> {
                when (destinationId) {
                    R.id.listFragment -> {
                        addButton?.visibility = View.VISIBLE
                        scanQRButton?.visibility = View.GONE
                    }
                    R.id.orderFragment -> {
                        scanQRButton?.visibility = View.VISIBLE
                        addButton?.visibility = View.GONE
                    }
                    else -> {
                        addButton?.visibility = View.GONE
                        scanQRButton?.visibility = View.GONE
                    }
                }
            }
            false -> {
                addButton?.visibility = View.GONE
                scanQRButton?.visibility = View.GONE
            }
        }
    }

    private fun updateNavigationVisibility(destinationId: Int) {
        when (destinationId) {
            R.id.splashFragment,
            R.id.loginFragment,
            R.id.registrationFragment -> {
                toolbar.visibility = View.GONE
                bottomNav.visibility = View.GONE
            }
            R.id.addBicycleFragment -> {
                toolbar.visibility = View.VISIBLE
                bottomNav.visibility = View.GONE
            }
            else -> {
                toolbar.visibility = View.VISIBLE
                bottomNav.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
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
                bottomNav.selectedItemId = selectedItemId
            }
        }
    }

    private fun setListenerBottomNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
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

    fun setAddBicycleButtonListener(listener: View.OnClickListener) {
        addButton?.setOnClickListener(listener)
    }

    fun setScanQRButtonListener(listener: View.OnClickListener) {
        scanQRButton?.setOnClickListener(listener)
    }
}
