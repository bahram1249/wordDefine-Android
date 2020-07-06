package com.example.worddefine.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.worddefine.R
import com.example.worddefine.Token
import com.example.worddefine.network.requestbody.Auth
import com.example.worddefine.network.WordDefineApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel(app: Application): AndroidViewModel(app){

    private val _context = getApplication<Application>()

    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean>
        get() = _navigateToMain

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage


    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    fun errorMessageDone(){
        _errorMessage.value = null
    }

    fun navigateToMainDone() {
        _navigateToMain.value = false
    }

    fun onLogin(auth: Auth){
        coroutineScope.launch {
            try {
                // send post request to server
                val webResponse = WordDefineApi.retrofitService.postAuthAsync(auth).await()

                if (webResponse.isSuccessful){
                    // save token and userId to shared preferences
                    Token.set(_context, webResponse.body()!!.result.token)
                    Token.setUserId(_context, webResponse.body()!!.result.userId)
                    _navigateToMain.value = true
                }
                else {
                    _errorMessage.value = webResponse.errorBody()!!.string()
                }

            } catch (e: IOException){
                _errorMessage.value = _context.getString(R.string.failed_to_connect_to_server)
            }
        }
    }

    init {
        _errorMessage.value = null
        _navigateToMain.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}