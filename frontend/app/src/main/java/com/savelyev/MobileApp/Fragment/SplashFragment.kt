package com.savelyev.MobileApp.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.*

class SplashFragment : Fragment() {
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(R.layout.fragment_splash, container, false)
        initComponents(root)
        when {
            !isUserRegistered() -> navController.navigate(R.id.registrationFragment)
            !isUserAuthorised() -> navController.navigate(R.id.loginFragment)
            !hasValidToken() -> navController.navigate(R.id.loginFragment)
            else -> navController.navigate(R.id.listFragment)
        }

        return root
    }

    private fun initComponents(root: View) {
        navController = NavHostFragment.findNavController(this)
        preferencesManager = PreferencesManager(requireContext())
    }

    private fun isUserRegistered(): Boolean {
        //preferencesManager.clearAll()
        Log.i("DebugInfo", preferencesManager.getAllData())
        return preferencesManager.getBoolean(PreferencesManager.APP_PREFERENCES_REGISTERED, false)
    }

    private fun isUserAuthorised(): Boolean {
        return preferencesManager.getBoolean(PreferencesManager.APP_PREFERENCES_AUTHORIZED, false)
    }

    private fun hasValidToken(): Boolean {
        val token = TokenManager.getInstance().getToken()
        return token != null && !JwtParser.isTokenExpired(token)
    }
}