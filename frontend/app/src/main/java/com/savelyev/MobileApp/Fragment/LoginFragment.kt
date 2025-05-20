package com.savelyev.MobileApp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.savelyev.MobileApp.Utils.PreferencesManager
import com.savelyev.MobileApp.Api.DTO.Request.AuthData
import com.savelyev.MobileApp.Api.Service.AuthService
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.*

class LoginFragment : Fragment() {
    private lateinit var authService: AuthService
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var navController: NavController

    private lateinit var phoneField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var loginBtn: MaterialButton
    private lateinit var goToRegistration: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_login, container, false)
        initComponents(root)
        setupListeners()

        return root
    }

    private fun initComponents(root: View) {
        navController = NavHostFragment.findNavController(this)
        preferencesManager = PreferencesManager(requireContext())
        authService = AuthService().apply { initialize(TokenManager.getInstance()) }

        phoneField = root.findViewById(R.id.etPhone)
        PhoneMaskHelper.applyPhoneMask(phoneField)
        passwordField = root.findViewById(R.id.etPassword)
        loginBtn = root.findViewById(R.id.btnLogin)
        goToRegistration = root.findViewById(R.id.tvRegister)
    }

    private fun setupListeners() {
        loginBtn.setOnClickListener {
            if (validateFields()) {
                loginUser()
            }
        }

        goToRegistration.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

    private fun validateFields(): Boolean {
        return when {
            phoneField.text.isNullOrEmpty() -> {
                phoneField.error = getString(R.string.MSG_ERROR_EMPTY_PHONE)
                false
            }
            passwordField.text.isNullOrEmpty() -> {
                passwordField.error = getString(R.string.MSG_ERROR_EMPTY_PASSWORD)
                false
            }
            else -> true
        }
    }

    private fun loginUser() {
        val authData = AuthData(
            numberPhone = phoneField.text.toString(),
            password = passwordField.text.toString()
        )

        authService.loginUser(authData) { success, errorMessage ->
            if (success) {
                handleSuccessfulLogin(authData.numberPhone)
            } else {
                PushManager.showToast(errorMessage ?: getString(R.string.MSG_ERROR_REGISTRATION_FAILED))
            }
        }
    }

    private fun handleSuccessfulLogin(phone: String) {
        preferencesManager.saveBoolean(PreferencesManager.APP_PREFERENCES_AUTHORIZED, true)

        UserManager.fetchAndSaveUser(phone) { success ->
            if (success) {
                navController.navigate(R.id.listFragment)
            } else {
                PushManager.showToast(getString(R.string.MSG_ERROR_USER_DATA_FETCH))
            }
        }
    }
}