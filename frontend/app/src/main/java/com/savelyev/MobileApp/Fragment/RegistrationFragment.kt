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
import com.savelyev.MobileApp.Api.DTO.Request.AuthData
import com.savelyev.MobileApp.Api.DTO.UserDTO
import com.savelyev.MobileApp.Api.Service.AuthService
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.PhoneMaskHelper
import com.savelyev.MobileApp.Utils.PreferencesManager
import com.savelyev.MobileApp.Utils.PushManager
import com.savelyev.MobileApp.Utils.TokenManager
import com.savelyev.MobileApp.Utils.UserManager

class RegistrationFragment : Fragment() {
    private var authService = AuthService()
    private lateinit var navController: NavController
    private lateinit var preferencesManager: PreferencesManager

    private lateinit var phoneField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var repeatPasswordField: TextInputEditText
    private lateinit var registrationButton: MaterialButton

    private lateinit var goToAuth: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_registration, container, false)
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
        repeatPasswordField = root.findViewById(R.id.etRepeatPassword)
        registrationButton = root.findViewById(R.id.btnRegister)
        goToAuth = root.findViewById(R.id.tvLogin)
    }

    private fun setupListeners() {
        registrationButton.setOnClickListener {
            if (validateFields()) {
                registerUser()
            }
        }

        goToAuth.setOnClickListener {
            navController.navigate(R.id.action_registrationFragment_to_loginFragment)
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
            passwordField.text?.length!! < 8 -> {
                passwordField.error = getString(R.string.MSG_ERROR_SIZE_PASSWORD)
                false
            }
            passwordField.text.toString() != repeatPasswordField.text.toString() -> {
                repeatPasswordField.error = getString(R.string.MSG_ERROR_MISMATCH_PASSWORD)
                false
            }
            else -> true
        }
    }

    private fun registerUser() {
        val authData = AuthData(
            numberPhone = phoneField.text.toString(),
            password = passwordField.text.toString()
        )

        authService.registerUser(authData) { success, errorMessage, userDTO ->
            if (success && userDTO != null) {
                handleSuccessfulRegistration(userDTO, authData)
            } else {
                PushManager.showToast(errorMessage ?: getString(R.string.MSG_ERROR_REGISTRATION_FAILED))
            }
        }
    }

    private fun handleSuccessfulRegistration(userDTO: UserDTO, authData: AuthData) {
        preferencesManager.saveBoolean(PreferencesManager.APP_PREFERENCES_REGISTERED, true)
        UserManager.setCurrentUser(userDTO)

        authService.loginUser(authData) { loginSuccess, loginError ->
            if (loginSuccess) {
                preferencesManager.saveBoolean(PreferencesManager.APP_PREFERENCES_AUTHORIZED, true)
                navController.navigate(R.id.listFragment)
            } else {
                PushManager.showToast(loginError ?: getString(R.string.MSG_ERROR_AUTO_AUTH_FAILED))
            }
        }
    }

}