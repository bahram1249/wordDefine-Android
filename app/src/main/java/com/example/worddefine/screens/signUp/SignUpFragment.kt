package com.example.worddefine.screens.signUp


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.example.worddefine.R

import com.example.worddefine.R.layout.fragment_sign_up
import com.example.worddefine.databinding.FragmentSignUpBinding
import com.example.worddefine.network.requestbody.User

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    private lateinit var viewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(activity).application
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            fragment_sign_up,
            container,
            false
        )

        binding.lifecycleOwner = this
        val viewModelFactory = SignUpViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SignUpViewModel::class.java)

        // validation form and submit to server
        validationForm()

        // error message of request show as toast
        viewModel.errorMessage.observe(this, Observer{
            if(null != it){
                Toast.makeText(context, viewModel.errorMessage.value, Toast.LENGTH_SHORT).show()
                viewModel.errorMessageDone()
            }
        })

        viewModel.navigateToMain.observe(this, Observer{
            if (it){
                // make toast for successful sign up
                Toast.makeText(context, getString(R.string.successful_sign_up), Toast.LENGTH_SHORT)
                    .show()
                // navigate to main fragment
                this.findNavController().navigate(SignUpFragmentDirections.actionToMainFragment())
                viewModel.navigateToMainDone()
            }
        })

        return binding.root
    }

    private fun validationForm() {
        form {
            input(binding.nameEdiTtext){
                length()
                    .greaterThan(2).lessThan(256)
                    .description(getString(R.string.greater_than_two_and_less_than_two_hundred_fifty_six))
            }
            input(binding.emailEdiTtext){
                isNotEmpty().description(getString(R.string.required))
                isEmail().description(getString(R.string.invalid_email_address))
            }
            input(binding.passwordEditText){
                isNotEmpty().description(getString(R.string.required))
                length().greaterThan(5).lessThan(256)
                    .description(getString(R.string.greater_than_five_and_less_than_two_hundred_fifty_six))
            }
            // send requestBody to authorization
            submitWith(binding.signUpButton){
                val user = User(
                    name = binding.nameEdiTtext.text.toString(),
                    email = binding.emailEdiTtext.text.toString(),
                    password = binding.passwordEditText.text.toString()
                )
                viewModel.onSignUp(user)
            }
        }
    }
}
