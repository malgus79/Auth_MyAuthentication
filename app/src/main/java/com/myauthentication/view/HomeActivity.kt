package com.myauthentication.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.myauthentication.core.MyAuthenticationApp
import com.myauthentication.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    fun signOut() {
        binding.btnSignOutSession.isEnabled = false
        AuthUI.getInstance().signOut(this)
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Hasta pronto", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                binding.btnSignOutSession.isEnabled = true
                binding.progressBar1.visibility = View.GONE
                Toast.makeText(this, "Ocurrio un error ${it.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}