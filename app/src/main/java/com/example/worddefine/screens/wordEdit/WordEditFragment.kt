package com.example.worddefine.screens.wordEdit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.afollestad.vvalidator.form
import com.example.worddefine.R
import com.example.worddefine.databinding.FragmentWordEditBinding


class WordEditFragment : Fragment() {

    private lateinit var viewModel: WordEditViewModel
    private lateinit var binding: FragmentWordEditBinding
    private lateinit var wordListId: String
    private var wordId: String? = null
    private var visiblePassword: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_word_edit,
            container,
            false
        )
        binding.lifecycleOwner = this

        initialArguments()

        if (visiblePassword)
            binding.passwordEditText.visibility = View.VISIBLE

        initialSpinnerAdapter()

        initialViewModel()

        validationForm()

        setupNavigateUp()

        return binding.root
    }

    private fun initialArguments() {
        wordId = WordEditFragmentArgs.fromBundle(this.arguments!!).wordId
        wordListId = WordEditFragmentArgs.fromBundle(this.arguments!!).wordListId
        visiblePassword = WordEditFragmentArgs.fromBundle(this.arguments!!).passwordVisible
    }

    private fun initialViewModel() {

        val application = requireNotNull(activity).application

        val viewModelFactory =
            WordEditViewModelFactory(application, Injection.provideWordEditRepository(context!!))

        viewModel =
            ViewModelProvider(this, viewModelFactory).get(WordEditViewModel::class.java)


        viewModel.resultMessage.observe(this, Observer {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.resultMessageDone()
            }
        })

        viewModel.navigateUp.observe(this, Observer {
            it?.let {
                this.findNavController().navigateUp()
                viewModel.navigateUpDone()
            }
        })

        viewModel.word.observe(this, Observer {
            it?.let {
                binding.nameEditText.setText(it.name)
                binding.definitionEditText.setText(it.definition)
                binding.examplesEditText.setText(it.examples)

                val adapter = binding.wordLanguageSpinner.adapter as ArrayAdapter<CharSequence>
                val position = adapter.getPosition(it.lang)

                binding.wordLanguageSpinner.setSelection(position)

                viewModel.onBindDone()
            }
        })

        // change button text and call bind data
        wordId?.let {
            binding.wordEditButton.text = getString(R.string.edit)
            viewModel.bindData(it)
        }



        binding.viewModel = viewModel
    }

    private fun initialSpinnerAdapter() {

        ArrayAdapter.createFromResource(
            context!!,
            R.array.word_languages_array,
            R.layout.support_simple_spinner_dropdown_item
        ).also { adapter ->
            // adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.wordLanguageSpinner.adapter = adapter
            if (wordId != null)
                binding.wordLanguageSpinner.setSelection(0)

        }
    }

    private fun setupNavigateUp() {
        val navController = this.findNavController()
        NavigationUI.setupWithNavController(binding.toolbar, navController)
    }

    private fun validationForm() {
        form {
            input(binding.nameEditText) {
                isNotEmpty().description(getString(R.string.required))
                length()
                    .greaterThan(2)
                    .lessThan(256)
                    .description(getString(R.string.greater_than_three_and_less_than_two_hundred_and_fifty_five))
            }

            input(binding.definitionEditText) {
                length()
                    .greaterThan(2).lessThan(1024)
                    .description(getString(R.string.greater_than_two_and_less_than_one_thousand_and_twenty_four))
            }

            input(binding.examplesEditText) {
                length()
                    .greaterThan(2).lessThan(2048)
                    .description(getString(R.string.greater_than_two_and_less_than_two_thousand_and_forty_eight))
            }

            if (visiblePassword)
                input(binding.passwordEditText) {
                    isNotEmpty().description("required")
                    length().greaterThan(2).lessThan(256)
                        .description(R.string.greater_than_two_and_less_than_two_hundred_fifty_six)
                }

            submitWith(binding.wordEditButton) {
                val lang = binding.wordLanguageSpinner.selectedItem.toString()

                var password: String? = null
                if (visiblePassword)
                    password = binding.passwordEditText.text.toString()

                viewModel.onEditButtonClick(
                    wordId,
                    binding.nameEditText.text.toString(),
                    binding.definitionEditText.text.toString(),
                    binding.examplesEditText.text.toString(),
                    lang,
                    password,
                    wordListId
                )
            }
        }
    }
}
