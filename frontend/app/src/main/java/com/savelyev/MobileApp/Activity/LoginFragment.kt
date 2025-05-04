package com.savelyev.MobileApp.Activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.savelyev.MobileApp.Utils.PreferencesManager
import com.savelyev.MobileApp.Utils.PreferencesManager.Companion
import com.savelyev.MobileApp.Api.DTO.AuthData
import com.savelyev.MobileApp.Api.Service.AuthService
import com.savelyev.MobileApp.R

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_login, container, false)
        val navController = NavHostFragment.findNavController(this)
        val preferencesManager = PreferencesManager(requireContext())
        val authService = AuthService()

        //Поля
        val number_phone_field = root.findViewById<EditText>(R.id.et_EmailField)
        val password_field = root.findViewById<EditText>(R.id.et_PasswordField)

        //Кнопка входа
        val enter_btn = root.findViewById<Button>(R.id.EnterButton)

        enter_btn.setOnClickListener {
            if (CheckEmptyField(number_phone_field, password_field)) {
                val authData = AuthData(number_phone_field.text.toString(), password_field.text.toString())
                authService.loginUser(authData) { success, response ->
                    if (success) {
                        preferencesManager.saveString(Companion.JWTTOKEN, response.toString())
                        preferencesManager.saveBoolean(Companion.APP_PREFERENCES_AUTHORIZED,true)
                        preferencesManager.saveBoolean(PreferencesManager.APP_PREFERENCES_REGISTERED, true)
                        Log.i("DebugInfo", preferencesManager.getAllData())
                        navController.navigate(R.id.listFragment)
                    } else {
                        ShowToast(response ?: "Авторизация провалилась.")
                    }
                }
            } else {
                ShowToast(R.string.MSG_ERROR_EMPTY_FIELD.toString())
            }
        }
        return root
    }


    private fun ShowToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun CheckEmptyField(enterField: EditText,passwordField: EditText): Boolean{
        val loginEmpty = enterField.text.isNotEmpty()
        val passwordEmpty = passwordField.text.isNotEmpty()

        return loginEmpty && passwordEmpty
    }

}




