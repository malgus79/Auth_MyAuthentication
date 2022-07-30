package com.myauthentication.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.myauthentication.R
import com.myauthentication.databinding.FragmentLoginBinding
import com.myauthentication.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)

        goLogin()

        return binding.root
    }

    private fun goLogin() {
        binding.tvHaveAccountLogIn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}