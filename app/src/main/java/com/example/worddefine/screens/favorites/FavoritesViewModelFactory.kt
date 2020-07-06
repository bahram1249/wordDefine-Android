package com.example.worddefine.screens.favorites

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.worddefine.data.FavoriteWordListRepository
import com.example.worddefine.data.WordListRepository

class FavoritesViewModelFactory(
    private val application: Application,
    private val repository: FavoriteWordListRepository
): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}