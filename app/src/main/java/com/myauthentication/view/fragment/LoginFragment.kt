package com.myauthentication.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.myauthentication.R
import com.myauthentication.core.MyAuthenticationApp
import com.myauthentication.databinding.FragmentLoginBinding
import com.myauthentication.model.data.LoginCredentials
import com.myauthentication.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)


        activityScope.launch {
            delay(1000)
            Toast.makeText(requireContext(), "Bienvenido", Toast.LENGTH_SHORT).show()
            val token = MyAuthenticationApp.prefs.getToken()
            if (token.isNotEmpty()){
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            } else {

            }
        }

        goSignUp()
        buttonEnable()
        validateFields()

        binding.btnLogin.setOnClickListener {
            attemptLogin(
                binding,
                binding.outlinedTextFieldEmail.editText?.text.toString(),
                binding.outlinedTextFieldPassword.editText?.text.toString()
            )
        }

        viewModel.loginStatus.observe(viewLifecycleOwner) {
            if (it) {
                goHome()
            } else {
                showDialogLoginError()
            }
        }

        return binding.root
    }

    private fun showErrorDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.error_dialog))
            .setMessage(
                getString(R.string.error_dialog_login)
            )
            .setPositiveButton(getString(R.string.ok)) { _, _ -> }
            .show()
    }

    // Navigation to Sign Up fragment
    private fun goSignUp() {
        binding.tvSignUp.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    // Navigation to Home
    private fun goHome() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    private fun validateFields() {
        val emailUI = binding.outlinedTextFieldEmail
        val passUI = binding.outlinedTextFieldPassword

        //Checks if email valid after changes on editText
        emailUI.editText?.doAfterTextChanged {

            viewModel.validateEmail(it.toString())

            if (!viewModel.validEmail) {
                emailUI.error = getString(R.string.invalid_email)
            } else {
                emailUI.error = null
            }
        }

        //Checks if password valid after changes on editText
        passUI.editText?.doAfterTextChanged {

            viewModel.validatePassword(it.toString())

            if (!viewModel.validPassword) {
                passUI.error = getString(R.string.invalid_pass)
            } else {
                passUI.error = null
            }
        }
    }

    //Enables login button if loginButtonLiveData is true
    private fun buttonEnable() {
        viewModel.loginButtonLiveData.observe(viewLifecycleOwner) {
            binding.btnLogin.isEnabled = it
        }
    }

    private fun attemptLogin(binding: FragmentLoginBinding, email: String, password: String) {
        viewModel.logIn(LoginCredentials(email, password))
        if (viewModel.loginStatus.value == true) {
            goHome()
        }
//        when (viewModel.postLoginStatus.value) {
//            LoginViewModel.LoginStatus.ERROR -> showDialogLoginError ()
//            LoginViewModel.LoginStatus.ERROR200 -> showDialogLoginError200 ()
//            else -> goHome()
//        }
    }

    //Dialog when error 200 in login
    private fun showDialogLoginError200() {
        binding.outlinedTextFieldEmail.editText?.error = getString(R.string.invalid_credentials)
        binding.outlinedTextFieldPassword.editText?.error = getString(R.string.invalid_credentials)
    }

    //Message error when invalid login
    private fun showDialogLoginError() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.error_dialog))
            .setMessage(
                getString(R.string.error_login_description)
            )
            .setPositiveButton(getString(R.string.ok)) { _, _ -> }
            .show()
    }

    private fun clearFields(binding: FragmentLoginBinding) {
        binding.outlinedTextFieldEmail.editText?.text?.clear()
        binding.outlinedTextFieldPassword.editText?.text?.clear()
    }
}