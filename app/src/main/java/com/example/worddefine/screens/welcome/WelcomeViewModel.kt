package com.example.worddefine.screens.welcome

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.worddefine.Token

class WelcomeViewModel(app: Application) : AndroidViewModel(app) {

    private val _context = getApplication<Application>()

    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean>
        get() = _navigateToMain

    init {

        // if user already login navigate to main fragment
        Token.get(_context)?.let {
            _navigateToMain.value = true
        }
    }
}
