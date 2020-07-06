package com.example.worddefine.screens.words

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.worddefine.data.WordRepository

class WordsViewModelFactory(private val application: Application,
                            private val wordRepository: WordRepository,
                            private val wordListId: String,
                            private val wordAccessToken: String): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordsViewModel::class.java)) {
            return WordsViewModel(application, wordRepository, wordListId, wordAccessToken) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}