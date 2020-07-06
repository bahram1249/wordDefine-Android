package com.example.worddefine.screens.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.worddefine.Token

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val _context = getApplication<Application>()

    private val _token = MutableLiveData<String>()

    init {
        Token.get(_context)?.let {
            _token.value = Token.get(_context)
        }
    }
}
