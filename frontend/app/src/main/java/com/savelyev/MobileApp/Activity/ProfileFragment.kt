package com.savelyev.MobileApp.Activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.PreferencesManager
import com.savelyev.MobileApp.Utils.PreferencesManager.Companion

class ProfileFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        val navController = NavHostFragment.findNavController(this)
        val preferencesManager = PreferencesManager(requireContext())
        val outFromAccount_button =  root.findViewById<Button>(R.id.btn_OutFromAccount)
        val deleteAccount_button =  root.findViewById<Button>(R.id.btn_DeleteUserAccount)

        outFromAccount_button.setOnClickListener {
            navController.navigate(R.id.splashFragment)
        }

        deleteAccount_button.setOnClickListener {
            preferencesManager.saveBoolean(PreferencesManager.APP_PREFERENCES_REGISTERED, false)
            navController.navigate(R.id.splashFragment)
        }

        return root
    }
}