package com.example.worddefine.screens.wordListEdit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.afollestad.vvalidator.form

import com.example.worddefine.R
import com.example.worddefine.databinding.FragmentWordListEditBinding
import com.example.worddefine.network.WordListsVisibleFilter

class WordListEditFragment : Fragment() {

    private lateinit var viewModel: WordListEditViewModel
    private lateinit var binding: FragmentWordListEditBinding
    private var wordListId: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_word_list_edit,
            container,
            false)
        binding.lifecycleOwner = this

        initialArguments()
        initialViewModel()
        initialSpinnerAdapter()
        validationForm()
        setupNavigateUp()

        return binding.root
    }

    private fun initialViewModel(){
        val application = requireNotNull(activity).application
        val viewModelFactory =
            WordListEditViewModelFactory(
                application, Injection.provideWordListEditRepository(context!!))

        viewModel =
            ViewModelProvider(this, viewModelFactory).get(WordListEditViewModel::class.java)

        // observing for showing toast message
        viewModel.resultMessage.observe(this, Observer {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.resultMessageDone()
            }
        })

        // observe to navigate up screen
        viewModel.navigateUp.observe(this, Observer {
            it?.let {
                findNavController().navigateUp()
                viewModel.navigateUpDone()
            }
        })

        // observe to bind data
        viewModel.wordList.observe(this, Observer {
            it?.let {
                binding.wordListTitleEditText.setText(it.title)

                val visiblePosition = when(it.visible){
                    WordListsVisibleFilter.SHOW_EVERYONE.value -> 0
                    WordListsVisibleFilter.SHOW_USER_WITH_PASSWORD.value -> 1
                    WordListsVisibleFilter.SHOW_ONLY_ME.value -> 2
                    else -> 0
                }
                binding.wordListVisibleFilterSpinner.setSelection(visiblePosition)

                val addWordByPosition = when(it.addWordBy){
                    WordListsVisibleFilter.SHOW_EVERYONE.value -> 0
                    WordListsVisibleFilter.SHOW_USER_WITH_PASSWORD.value -> 1
                    WordListsVisibleFilter.SHOW_ONLY_ME.value -> 2
                    else -> 0
                }
                binding.wordListAddWordByFilterSpinner.setSelection(addWordByPosition)

                viewModel.onBindDone()
            }
        })

        // change button text and call bind data
        wordListId?.let {
            binding.wordListEditButton.text = getString(R.string.edit)
            viewModel.bindData(it)
        }

        binding.viewModel = viewModel
    }

    private fun initialSpinnerAdapter() {
        
        ArrayAdapter.createFromResource(
            context!!,
            R.array.word_list_visible_filter_array,
            R.layout.support_simple_spinner_dropdown_item
        ).also { adapter ->
            // adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.wordListVisibleFilterSpinner.adapter = adapter
            if (wordListId != null)
                binding.wordListVisibleFilterSpinner.setSelection(0)

            binding.wordListAddWordByFilterSpinner.adapter = adapter
            if (wordListId != null)
                binding.wordListAddWordByFilterSpinner.setSelection(0)

            // change Visibility of wordListPassword
            binding.wordListVisibleFilterSpinner.onItemSelectedListener =
                SpinnerItemSelected(
                    binding.wordListVisibleFilterSpinner,
                    binding.wordListAddWordByFilterSpinner,
                    binding.wordListPasswordEditText
                )

            // change Visibility of wordListPassword
            binding.wordListAddWordByFilterSpinner.onItemSelectedListener =
                SpinnerItemSelected(
                    binding.wordListVisibleFilterSpinner,
                    binding.wordListAddWordByFilterSpinner,
                    binding.wordListPasswordEditText
                )

        }
    }

    private fun initialArguments() {
        wordListId = WordListEditFragmentArgs.fromBundle(this.arguments!!).wordListId
    }

    private fun setupNavigateUp(){
        val navController = this.findNavController()
        NavigationUI.setupWithNavController(binding.toolbar, navController)
    }

    private fun validationForm() {
        form {
            input(binding.wordListTitleEditText){
                isNotEmpty().description(getString(R.string.required))
                length()
                    .greaterThan(2)
                    .lessThan(256)
                    .description(getString(R.string.greater_than_three_and_less_than_two_hundred_and_fifty_five))
            }

            val visibleFilterPosition = binding.wordListVisibleFilterSpinner.selectedItemPosition
            val addWordByPosition = binding.wordListAddWordByFilterSpinner.selectedItemPosition

            if (visibleFilterPosition == 1 || addWordByPosition == 1){
                input(binding.wordListPasswordEditText){
                    length()
                        .greaterThan(5)
                        .lessThan(256)
                        .description(getString(R.string.greater_than_five_and_less_than_two_hundred_fifty_six))
                }
            }
            submitWith(binding.wordListEditButton){

                val visibleFilter =
                    getVisibleFilterBasedOnSpinnerPosition(
                        binding.wordListVisibleFilterSpinner.selectedItemPosition)
                val addWordBy =
                    getVisibleFilterBasedOnSpinnerPosition(
                        binding.wordListAddWordByFilterSpinner.selectedItemPosition)

                var password : String? = null
                if(binding.wordListPasswordEditText.text.isNotEmpty()){
                    password = binding.wordListPasswordEditText.text.toString()
                }
                
                viewModel.onEditButtonClick(
                    wordListId,
                    binding.wordListTitleEditText.text.toString(),
                    visibleFilter,
                    addWordBy,
                    password
                )
            }
        }
    }

    private fun getVisibleFilterBasedOnSpinnerPosition(position: Int): WordListsVisibleFilter{
        return when(position){
            0 -> WordListsVisibleFilter.SHOW_EVERYONE
            1 -> WordListsVisibleFilter.SHOW_USER_WITH_PASSWORD
            2 -> WordListsVisibleFilter.SHOW_ONLY_ME
            else -> WordListsVisibleFilter.SHOW_EVERYONE
        }
    }

    private class SpinnerItemSelected(
        private val wordListVisibleFilterSpinner: AppCompatSpinner,
        private val wordListAddWordByFilterSpinner: AppCompatSpinner,
        private val wordListPasswordEditText: EditText
    ): AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            TODO("Not yet implemented")
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val visibleFilterPosition = wordListVisibleFilterSpinner.selectedItemPosition
            val addWordByPosition = wordListAddWordByFilterSpinner.selectedItemPosition

            if (visibleFilterPosition != 1 && addWordByPosition != 1){
                wordListPasswordEditText.visibility = View.INVISIBLE
            } else {
                wordListPasswordEditText.visibility = View.VISIBLE
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
    
}
