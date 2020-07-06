package com.example.worddefine.screens.word

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI

import com.example.worddefine.R
import com.example.worddefine.databinding.FragmentWordBinding

class WordFragment : Fragment() {


    private lateinit var binding: FragmentWordBinding
    private lateinit var wordId: String
    private lateinit var wordName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(activity).application

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_word,
            container,
            false)

        binding.lifecycleOwner = this

        initializeArguments()

        val viewModelFactory = WordViewModelFactory(application, wordId)
        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(WordViewModel::class.java)

        binding.viewModel = viewModel

        val navController = this.findNavController()
        NavigationUI.setupWithNavController(binding.toolbar, navController)

        return binding.root
    }

    private fun initializeArguments() {
        wordId = WordFragmentArgs.fromBundle(this.arguments!!).wordId
        wordName = WordFragmentArgs.fromBundle(this.arguments!!).wordName
    }


}
