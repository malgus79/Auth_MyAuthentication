package com.myauthentication.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.myauthentication.core.MyAuthenticationApp
import com.myauthentication.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val activityScope = CoroutineScope(Dispatchers.Main)
    private val authUser: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Visible progress bar
        binding.progressBar1.isVisible = true

        //1 second delay to check if there is a saved token
        activityScope.launch {
            delay(1000)
            val token = MyAuthenticationApp.prefs.getToken()
            val auth = authUser.currentUser

            //Existing token -> go to home
            if (token.isNotEmpty() || (auth != null)) {
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                Toast.makeText(this@MainActivity, "Bienvenido", Toast.LENGTH_SHORT).show()
                binding.progressBar1.isVisible = false
                finish()

            //Token = empty -> navigate to the login
            } else {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                delay(2000)
                binding.progressBar1.isVisible = false
            }
        }
    }
}

//ID App Facebook: 1729820294054897
//Clave secreta de la app: 9feb1c10c4c26574f3487d905c69bcb1
//RI de redireccionamiento de OAuth: https://myauthentication-8de41.firebaseapp.com/__/auth/handler
// 2jmj7l5rSw0yVb/vlWAYkK/YBwk=
//Clave de descifrado del referente de instalación: b9b4dfbd04a20bbbb4d459744a779ad2fa81aaca4489b4542f577dde47ba23c1