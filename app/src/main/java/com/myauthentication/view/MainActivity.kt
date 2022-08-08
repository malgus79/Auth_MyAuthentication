package com.myauthentication.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.myauthentication.core.MyAuthenticationApp
import com.myauthentication.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Visible progress bar
        binding.progressBar1.isVisible = true
        binding.btnSignOutSession.isVisible = false
        binding.btnQuit.isVisible = false

        //1 second delay to check if there is a saved token
        activityScope.launch {
            delay(1000)
            val token = MyAuthenticationApp.prefs.getToken()

            //Token = empty -> navigate to the loginFragment
            if (token.isEmpty()) {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                delay(2000)
                binding.progressBar1.isVisible = false
                binding.btnSignOutSession.isVisible = true
                binding.btnQuit.isVisible = true

                //Existing token -> go to home
            } else if (token.isNotEmpty()) {
                Toast.makeText(this@MainActivity, "Bienvenido", Toast.LENGTH_SHORT).show()
                binding.progressBar1.isVisible = false
                binding.btnSignOutSession.isVisible = true
                binding.btnQuit.isVisible = true
            }
        }

        //Log out and exit the app. Clear the token
        binding.btnSignOutSession.setOnClickListener {
            signOut()
            MyAuthenticationApp.prefs.deleteToken()
        }

        //Exit without logging out
        binding.btnQuit.setOnClickListener {
            finish()
        }
    }

    //Method to exit the app
    private fun signOut() {
        finish()
    }

        override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}