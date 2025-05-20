package com.savelyev.MobileApp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.PreferencesManager
import com.savelyev.MobileApp.Utils.UserManager

class ProfileFragment : Fragment() {
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var navController: NavController

    private lateinit var userName: TextView
    private lateinit var userRole: TextView
    private lateinit var historyOrderLayout: LinearLayout
    private lateinit var outAccountButton: Button
    private lateinit var deleteAccountButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        initComponents(root)
        setupListeners()

        setUserData()
        return root
    }


    private fun initComponents(root: View) {
        navController = NavHostFragment.findNavController(this)
        preferencesManager = PreferencesManager(requireContext())

        userName = root.findViewById(R.id.tv_user_name)
        userRole = root.findViewById(R.id.tv_membership_status)
        historyOrderLayout = root.findViewById(R.id.history_order_layout)
        outAccountButton = root.findViewById(R.id.btn_logout)
        deleteAccountButton = root.findViewById(R.id.btn_delete_account)
    }

    private fun setupListeners() {
        historyOrderLayout.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_orderHistoryFragment)
        }

        outAccountButton.setOnClickListener {
            preferencesManager.saveBoolean(PreferencesManager.APP_PREFERENCES_AUTHORIZED, false)
            navController.navigate(R.id.splashFragment)
        }
        deleteAccountButton.setOnClickListener {
            navController.navigate(R.id.splashFragment)
        }
    }


    private fun setUserData(){
        val currentUser = UserManager.getCurrentUser()
        userName.text = currentUser?.username
        userRole.text = when {
            UserManager.isAdmin() -> "Администратор"
            UserManager.isManager() -> "Менеджер"
            else -> "Пользователь"
        }
    }

}