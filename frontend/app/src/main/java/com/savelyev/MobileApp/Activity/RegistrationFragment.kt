package com.savelyev.MobileApp.Activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.savelyev.MobileApp.Api.DTO.AuthData
import com.savelyev.MobileApp.Api.Service.AuthService
import com.savelyev.MobileApp.R
import com.savelyev.MobileApp.Utils.PreferencesManager
import com.savelyev.MobileApp.Utils.PreferencesManager.Companion

class RegistrationFragment : Fragment() {
    private val authService = AuthService()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_registration, container, false)
        val navController = NavHostFragment.findNavController(this)
        val preferencesManager = PreferencesManager(requireContext())

        val number_phone_field = root.findViewById<EditText>(R.id.et_NumberPhoneField)
        val password_field = root.findViewById<EditText>(R.id.et_PasswordField)
        val repeat_password_field = root.findViewById<EditText>(R.id.et_RepeatPasswordField)
        val registration_button = root.findViewById<Button>(R.id.RegistrationButton)
        val goToAuth = root.findViewById<TextView>(R.id.tv_GoToAuthorisation)


        registration_button.setOnClickListener {
            val authData = AuthData(number_phone_field.text.toString(), password_field.text.toString())
            authService.registerUser(authData) { success, errorMessage ->
                if (success) {
                    preferencesManager.saveBoolean(PreferencesManager.APP_PREFERENCES_REGISTERED, true)
                    authService.loginUser(authData) { _, _ -> }
                    Log.i("DebugInfo", preferencesManager.getAllData())
                    navController.navigate(R.id.listFragment)
                } else {
                    ShowToast(errorMessage ?:"Регистрация провалилась.")
                }
            }
//            if (CheckEmptyField(enter_field, password_field, repeat_password_field)) {
//                auth.createUserWithEmailAndPassword(
//                    enter_field.text.toString(),
//                    password_field.text.toString()
//                )
//                    .addOnCompleteListener { task: Task<AuthResult> ->
//                        if (task.isSuccessful) {
//                            AddDataBoolean(storage, APP_PREFERENCES_registered, true)
//                            AddDataBoolean(storage, APP_PREFERENCES_auto_login, false)
//                            navController.navigate(R.id.listFragment)
//                        }
//                    }.addOnFailureListener {
//                        ErrorHandling(enter_field, password_field, repeat_password_field)
//                    }
//            }
//            else {
//                ShowToast(R.string.msg_error_EmptyField)
//            }
        }

        goToAuth.setOnClickListener {
            navController.navigate(R.id.loginFragment)
        }

        return root
    }



    private fun ShowToast(Error: String){
        Toast.makeText(requireContext(), Error, Toast.LENGTH_SHORT).show()
    }

    private fun CheckEmptyField(EnterField: EditText,PasswordField: EditText, RepeatPasswordField: EditText): Boolean{
        val loginEmpty = EnterField.text.isNotEmpty()
        val passwordEmpty = PasswordField.text.isNotEmpty()
        val  repeatPasswordEmpty = RepeatPasswordField.text.isNotEmpty()

        return loginEmpty && passwordEmpty && repeatPasswordEmpty
    }

    private fun ErrorHandling(EnterField: EditText,PasswordField: EditText, RepeatPasswordField: EditText): Boolean{
        when {
            PasswordField.text.length in 1..7 -> {
                ShowToast(R.string.msg_error_SizePassword.toString())
                return false
            }

            PasswordField.text.toString() != RepeatPasswordField.text.toString() -> {
                ShowToast(R.string.MSG_ERROR_MISMATCH_PASSWORD.toString())
                return false
            }
        }
        return true
    }
}