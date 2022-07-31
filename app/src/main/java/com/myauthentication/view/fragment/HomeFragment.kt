package com.myauthentication.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.myauthentication.R
import com.myauthentication.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        binding.btnHome.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        binding.btnSignOut.setOnClickListener {

        }

        return binding.root
    }
}