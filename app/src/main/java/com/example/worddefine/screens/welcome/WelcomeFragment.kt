package com.example.worddefine.screens.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.example.worddefine.R
import com.example.worddefine.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private lateinit var viewModel: WelcomeViewModel

    private lateinit var binding: FragmentWelcomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(activity).application

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_welcome,
            container,
            false
        )

        binding.lifecycleOwner = this

        val viewModelFactory = WelcomeViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(WelcomeViewModel::class.java)

        // navigate to login fragment
        binding.loginButton.setOnClickListener {
            this.findNavController().navigate(WelcomeFragmentDirections.actionToLoginFragment())
        }

        // navigate to sign up fragment
        binding.registerText.setOnClickListener {
            this.findNavController().navigate(WelcomeFragmentDirections.actionToSignUpFragment())
        }

        // navigate to main fragment
        viewModel.navigateToMain.observe(this, Observer {
            if(it){
                this.findNavController().navigate(WelcomeFragmentDirections.actionToMainFragment())
            }
        })

        return binding.root
    }


}
