package com.myauthentication.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.myauthentication.R
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
        binding.btnHome.isVisible = false
        binding.btnSignOut.isVisible = false

        activityScope.launch {
            delay(2000)
            binding.progressBar1.isVisible = false
            binding.btnHome.isVisible = true
            binding.btnSignOut.isVisible = true
        }

        binding.btnHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            prefs.deleteToken()
        }

        binding.btnSignOut.setOnClickListener {
            signOut()
        }

        return binding.root
    }

    private fun signOut() {
        activity?.finish()
    }
}