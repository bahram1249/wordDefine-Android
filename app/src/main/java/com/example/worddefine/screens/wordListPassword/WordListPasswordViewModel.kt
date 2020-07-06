package com.example.worddefine.screens.wordListPassword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.worddefine.R
import com.example.worddefine.Token
import com.example.worddefine.data.WordListPasswordRepository
import com.example.worddefine.data.response.ResponseStatus
import com.example.worddefine.data.response.ResponseType
import com.example.worddefine.database.WordDefineDatabase
import com.example.worddefine.database.model.WordListToken
import timber.log.Timber

class WordListPasswordViewModel(
    app: Application,
    private val wordListPasswordRepository: WordListPasswordRepository,
    private val wordListId: String,
    private val wordListTitle: String) : AndroidViewModel(app) {

    private val _context = getApplication<Application>()
    private val _database: WordDefineDatabase = WordDefineDatabase.getInstance(_context)!!

    private val _token = MutableLiveData<String>()

    private val _resultMessage = MutableLiveData<String?>()
    val resultMessage: LiveData<String?>
        get() = _resultMessage

    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage


    private val _navigateToWords = MutableLiveData<WordListToken>()
    val navigateToWords: LiveData<WordListToken>
        get() = _navigateToWords


    fun resultMessageDone(){
        _resultMessage.value = null
    }

    fun snackMessageDone(){
        _snackMessage.value = null
    }

    fun onNavigateToWordsDone(){
        _navigateToWords.value = null
    }

    private fun onDone(responseStatus: ResponseStatus, wordListId: String){
        when(responseStatus.type){

            ResponseType.Successful -> {
                Timber.d("submit word list password, ${responseStatus.type}")
                
                // navigate to words
                val wordListToken = _database.wordListTokenDao().getTokenByWordListId(wordListId)
                navigateToWords(wordListToken)
            }


            ResponseType.RequestInProgress ->
                Timber.d("submit word list password, ${responseStatus.type}")

            ResponseType.NotAccess ->
                _resultMessage.postValue(_context.getString(R.string.the_password_you_set_is_wrong))

            ResponseType.NotFound ->
                _resultMessage.postValue(responseStatus.message)

            ResponseType.WebServiceError ->
                _resultMessage.postValue(responseStatus.message)

            ResponseType.FailedToConnect ->
                _snackMessage.postValue(_context.getString(R.string.failed_to_connect_to_server))

        }
    }

    private fun navigateToWords(wordListToken: WordListToken?) {
        if (wordListToken == null) return
        _navigateToWords.postValue(wordListToken)
    }

    fun submitWordListPassword(password: String) {
        wordListPasswordRepository.requestToGetWordListToken(
            _token.value!!,
            wordListId,
            wordListTitle,
            password){

            onDone(it, wordListId)

        }
    }

    init {
        _token.value = Token.get(_context)
    }
}
