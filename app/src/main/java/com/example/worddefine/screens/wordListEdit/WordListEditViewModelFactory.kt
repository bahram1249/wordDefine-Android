package com.example.worddefine.screens.wordListEdit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.worddefine.data.WordListEditRepository

class WordListEditViewModelFactory(
    private val application: Application,
    private val wordListEditRepository: WordListEditRepository) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordListEditViewModel::class.java)) {
            return WordListEditViewModel(application, wordListEditRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}