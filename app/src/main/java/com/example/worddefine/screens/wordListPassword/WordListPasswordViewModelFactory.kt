package com.example.worddefine.screens.wordListPassword

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.worddefine.data.WordListPasswordRepository
import com.example.worddefine.database.model.WordList

class WordListPasswordViewModelFactory(private val application: Application,
                                       private val wordListPasswordRepository: WordListPasswordRepository,
                                       private val wordListId: String,
                                       private val wordListTitle: String): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordListPasswordViewModel::class.java)) {
            return WordListPasswordViewModel(
                application, wordListPasswordRepository, wordListId, wordListTitle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}