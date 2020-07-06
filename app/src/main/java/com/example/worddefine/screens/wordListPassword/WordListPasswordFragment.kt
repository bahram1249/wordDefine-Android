package com.example.worddefine.screens.wordListPassword

import androidx.lifecycle.ViewModelProviders
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
import androidx.navigation.ui.NavigationUI
import com.afollestad.vvalidator.form

import com.example.worddefine.R
import com.example.worddefine.databinding.FragmentWordListPasswordBinding
import com.google.android.material.snackbar.Snackbar

class WordListPasswordFragment : Fragment() {

    private lateinit var binding: FragmentWordListPasswordBinding
    private lateinit var viewModel: WordListPasswordViewModel
    private lateinit var wordListId: String
    private lateinit var wordListTitle: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(activity).application

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_word_list_password,
            container,
            false)

        binding.lifecycleOwner = this

        initializeArguments()

        val viewModelFactory = WordListPasswordViewModelFactory(
            application,
            Injection.provideWordListPasswordRepository(context!!),
            wordListId,
            wordListTitle)

        viewModel =
            ViewModelProvider(this, viewModelFactory)
                .get(WordListPasswordViewModel::class.java)


        viewModel.navigateToWords.observe(this, Observer {
            it?.let {
                this.findNavController().navigate(
                    WordListPasswordFragmentDirections
                        .actionWordListPasswordFragmentToWordsFragment(
                            it.token,
                            it.wordListId,
                            it.wordListTitle))

                viewModel.onNavigateToWordsDone()
            }
        })

        viewModel.resultMessage.observe(this, Observer {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.resultMessageDone()
            }

        })

        viewModel.snackMessage.observe(this, Observer {
            it?.let {
                Snackbar.make(binding.setPasswordButton, it, Snackbar.LENGTH_SHORT).show()
                viewModel.snackMessageDone()
            }
        })

        binding.viewModel = viewModel

        validationForm()

        val navController = this.findNavController()
        NavigationUI.setupWithNavController(binding.toolbar, navController)

        return binding.root
    }

    private fun validationForm() {
        form {
            input(binding.passwordEditText){
                isNotEmpty().description(R.string.required)
            }
            submitWith(binding.setPasswordButton){
                viewModel.submitWordListPassword(binding.passwordEditText.text.toString())
            }
        }
    }

    private fun initializeArguments() {
        wordListId = WordListPasswordFragmentArgs.fromBundle(this.arguments!!).wordListId
        wordListTitle = WordListPasswordFragmentArgs.fromBundle(this.arguments!!).wordListTitle
    }


}
