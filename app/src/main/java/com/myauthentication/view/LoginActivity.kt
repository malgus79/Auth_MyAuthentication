package com.myauthentication.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.myauthentication.databinding.ActivityLoginBinding
import com.myauthentication.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var navController: NavController
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Navigation settings
        val navHostFragment =
            supportFragmentManager.findFragmentById(androidx.navigation.fragment.R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.findNavController()

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 200) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            viewModel.endUpGoogleLogIn(task)

        }
    }
        override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}