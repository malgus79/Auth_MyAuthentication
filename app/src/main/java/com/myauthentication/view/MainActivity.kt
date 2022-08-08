package com.myauthentication.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.myauthentication.core.MyAuthenticationApp
import com.myauthentication.databinding.ActivityMainBinding
import com.myauthentication.view.fragment.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val activityScope = CoroutineScope(Dispatchers.Main)
    private val authUser: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        goTo()

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

//    private fun goTo(){
//        if(authUser.currentUser != null){
//            startActivity(Intent(this,HomeActivity::class.java))
//            finish()
//        }else{
//            startActivity(Intent(this,LoginActivity::class.java))
//            finish()
//        }
//    }
}