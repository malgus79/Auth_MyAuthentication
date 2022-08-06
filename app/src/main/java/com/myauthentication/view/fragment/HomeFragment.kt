package com.myauthentication.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.myauthentication.R
import com.myauthentication.core.MyAuthenticationApp
import com.myauthentication.core.MyAuthenticationApp.Companion.prefs
import com.myauthentication.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)


        binding.progressBar1.isVisible = true
        binding.btnSignOutSession.isVisible = false
        binding.btnQuit.isVisible = false

        activityScope.launch {
            delay(1000)
            val token = MyAuthenticationApp.prefs.getToken()
            if (token.isEmpty()) {
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                binding.progressBar1.isVisible = false
                binding.btnSignOutSession.isVisible = true
                binding.btnQuit.isVisible = true

            } else if (token.isNotEmpty()) {
                Toast.makeText(requireContext(), "Bienvenido", Toast.LENGTH_SHORT).show()
                binding.progressBar1.isVisible = false
                binding.btnSignOutSession.isVisible = true
                binding.btnQuit.isVisible = true
            }
        }

        binding.btnSignOutSession.setOnClickListener {
            signOut()
            prefs.deleteToken()
        }

        binding.btnQuit.setOnClickListener {
            signOut()
        }

        return binding.root
    }

    private fun signOut() {
        activity?.finish()
    }
}