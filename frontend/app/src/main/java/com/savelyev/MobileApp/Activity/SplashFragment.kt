package com.savelyev.MobileApp.Activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.PreferencesManager
import com.savelyev.MobileApp.Utils.PreferencesManager.Companion

class SplashFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root =  inflater.inflate(R.layout.fragment_splash, container, false)
        val navController = NavHostFragment.findNavController(this)
        val preferencesManager = PreferencesManager(requireContext())
        if (CheckUserRegistred(preferencesManager)) {
            if (CheckJWTToken()) {
                navController.navigate(R.id.listFragment)
            }
            else {
                navController.navigate(R.id.loginFragment)
            }
        }
        else {
            navController.navigate(R.id.registrationFragment)
        }
        return root
    }
    fun CheckUserRegistred(preferencesManager: PreferencesManager): Boolean {
        //preferencesManager.clearAll()
        Log.i("DebugInfo", preferencesManager.getAllData())
        val userLogin = preferencesManager.getBoolean(Companion.APP_PREFERENCES_REGISTERED, false)
        return userLogin
    }
    fun CheckJWTToken(): Boolean {
        return true; // TODO: Сделать проверку на актуализацию токена
    }
}