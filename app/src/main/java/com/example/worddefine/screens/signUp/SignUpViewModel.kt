package com.example.worddefine.screens.signUp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.worddefine.R
import com.example.worddefine.Token
import com.example.worddefine.network.WordDefineApi
import com.example.worddefine.network.requestbody.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class SignUpViewModel(app: Application): AndroidViewModel(app) {

    private val _context = getApplication<Application>()

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean>
        get() = _navigateToMain

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun errorMessageDone(){
        _errorMessage.value = null
    }

    fun navigateToMainDone(){
        _navigateToMain.value = false
    }

    fun onSignUp(user: User){
        coroutineScope.launch {
            try {
                // send post request to server
                val webResponse = WordDefineApi.retrofitService.postUsersAsync(user).await()

                if (webResponse.isSuccessful){
                    // save token and userId to shared preferences
                    Token.set(_context, webResponse.headers().get("x-auth-token")!!)
                    Token.setUserId(_context,webResponse.body()!!.result.id)
                    _navigateToMain.value = true
                } else {
                    _errorMessage.value = webResponse.errorBody()!!.string()
                }

            } catch (e: IOException){
                _errorMessage.value = _context.getString(R.string.failed_to_connect_to_server)
            }
        }
    }

    init {
        _navigateToMain.value = false
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}