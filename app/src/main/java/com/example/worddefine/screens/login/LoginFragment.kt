package com.example.worddefine.screens.login


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
import com.example.worddefine.R.layout.fragment_login
import com.example.worddefine.databinding.FragmentLoginBinding
import com.example.worddefine.network.requestbody.Auth


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(activity).application
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            fragment_login,
            container,
            false
        )

        binding.lifecycleOwner = this
        val viewModelFactory = LoginViewModelFactory(application)
        // get instance of viewModel
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
        // set view Model in data binding
        binding.loginViewModel = viewModel

        // validation form and send request for login
        validationForm()

        viewModel.errorMessage.observe(this, Observer{
            if(null != it){
                // enable login button after the request failed
                binding.loginButton.isEnabled = true
                // request error message show as toast
                Toast.makeText(context, viewModel.errorMessage.value, Toast.LENGTH_SHORT).show()
                viewModel.errorMessageDone()
            }
        })

        // navigate to main fragment
        viewModel.navigateToMain.observe(this, Observer {
            if(it){
                this.findNavController().navigate(LoginFragmentDirections.actionToMainFragment())
                viewModel.navigateToMainDone()
            }
        })
        return binding.root
    }

    private fun validationForm() {
        form {
            input(binding.emailEditText){
                isNotEmpty().description(getString(R.string.required))
                isEmail().description(getString(R.string.invalid_email_address))
            }
            input(binding.passwordEditText){
                isNotEmpty().description(getString(R.string.required))
                length()
                    .greaterThan(5).lessThan(256)
                    .description(getString(R.string.greater_than_five_and_less_than_two_hundred_fifty_six))
            }
            // send requestBody to authorization
            submitWith(binding.loginButton) {
                // disable login button until the result of request comeback
                binding.loginButton.isEnabled = false
                val auth = Auth(
                    email = binding.emailEditText.text.toString(),
                    password = binding.passwordEditText.text.toString()
                )
                viewModel.onLogin(auth)
            }
        }
    }
}
