package com.myauthentication.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
//import com.facebook.AccessToken
//import com.facebook.CallbackManager
//import com.facebook.FacebookCallback
//import com.facebook.FacebookException
//import com.facebook.login.LoginManager
//import com.facebook.login.LoginResult
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myauthentication.R
import com.myauthentication.core.MyAuthenticationApp
import com.myauthentication.databinding.FragmentLoginBinding
import com.myauthentication.model.data.LoginCredentials
import com.myauthentication.view.HomeActivity
import com.myauthentication.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        private const val RC_SIGN_IN = 123
    }

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    private val activityScope = CoroutineScope(Dispatchers.Main)
//    private val facebookCallbackManager = CallbackManager.Factory.create()
    private lateinit var auth: FirebaseAuth
    private val TAG = "Testing"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        auth = Firebase.auth

        //1 second delay to check if there is a saved token
        activityScope.launch {
            delay(1000)
            val token = MyAuthenticationApp.prefs.getToken()
            //Token != empty -> navigate to the homeFragment
            if (token.isNotEmpty()) {
                startActivity(Intent(requireContext(), HomeActivity::class.java))
                Toast.makeText(requireContext(), "Bienvenido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Por favor, ingrese sus datos", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        goSignUp()
        buttonEnable()
        validateFields()
        googleLogin()
//        facebookLogIn()

        binding.btnLogin.setOnClickListener {
            attemptLogin(
                binding,
                binding.outlinedTextFieldEmail.editText?.text.toString(),
                binding.outlinedTextFieldPassword.editText?.text.toString()
            )
        }

        //Login status
        viewModel.loginStatus.observe(viewLifecycleOwner) {
            if (it) {
                goHome()
            } else {
                showDialogLoginError()
            }
        }

        return binding.root
    }

    //Navigation to Sign Up fragment
    private fun goSignUp() {
        binding.tvSignUp.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    // Navigation to Home
    private fun goHome() {
        startActivity(Intent(requireContext(), HomeActivity::class.java))
        Toast.makeText(requireContext(), "Bienvenido", Toast.LENGTH_SHORT).show()
    }

    //Validation of text fields
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

    //login attempt
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

    //Method to delete the text of the fields
    private fun clearFields(binding: FragmentLoginBinding) {
        binding.outlinedTextFieldEmail.editText?.text?.clear()
        binding.outlinedTextFieldPassword.editText?.text?.clear()
    }

    //Login with google
    fun googleLogin() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build())

        binding.btnGoogleLogin.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false)
                    .build(),
                RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)

        try {
            if (requestCode == RC_SIGN_IN) {
                val response = IdpResponse.fromResultIntent(data)

                if (resultCode == Activity.RESULT_OK) {
                    // Successfully signed in
                    val user = FirebaseAuth.getInstance().currentUser
                    Toast.makeText(requireContext(),
                        "Bienvenid@ ${user!!.displayName}",
                        Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), HomeActivity::class.java))

                } else {
                    // Sign in failed. If response is null the user canceled the
                    // sign-in flow using the back button. Otherwise check
                    // response.getError().getErrorCode() and handle the error.
                    // ...
                    Toast.makeText(requireContext(),
                        "Ocurrio un error ${response!!.error!!.errorCode}",
                        Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(),
                "Ocurrio un error al seleccionar una cuenta",
                Toast.LENGTH_SHORT).show()
        }
    }

//    override fun onStart() {
//        super.onStart()
//        val currentUser = auth.currentUser
//        if (currentUser != null){
//            goHome()
//        }
//    }

//    private fun facebookLogIn() {
//        binding.btnFacebookLogin.setOnClickListener {
//            Log.d(TAG, "facebook:ButtonClick")
//            LoginManager.getInstance()
//                .logInWithReadPermissions(requireActivity(), listOf("email","public_profile"))
//
//            // Registers a login callback
//            LoginManager.getInstance().registerCallback(
//                facebookCallbackManager,
//                object : FacebookCallback<LoginResult> {
//                    override fun onCancel() {
//                        Log.d(TAG, "facebook:onCancel")
//                        showDialogLoginError()
//                    }
//                    override fun onError(error: FacebookException) {
//                        Log.d(TAG, "facebook:onError", error)
//                        showDialogLoginError()
//                    }
//                    override fun onSuccess(result: LoginResult) {
//                        Log.d(TAG, "facebook:onSuccess:$result")
//                        result?.let { val token = it.accessToken }
//                        handleFacebookAccessToken(result.accessToken)
//                    }
//                })
//        }
//    }

//    private fun handleFacebookAccessToken(token: AccessToken) {
//        Log.d(TAG, "handleFacebookAccessToken:$token")
//
//        val credential = FacebookAuthProvider.getCredential(token.token)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
//                    goHome()
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    showDialogLoginError()
//                }
//        }
//    }
}