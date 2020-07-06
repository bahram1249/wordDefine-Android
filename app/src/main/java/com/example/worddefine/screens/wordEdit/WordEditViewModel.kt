package com.example.worddefine.screens.wordEdit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.worddefine.R
import com.example.worddefine.Token
import com.example.worddefine.data.WordEditRepository
import com.example.worddefine.data.response.ResponseStatus
import com.example.worddefine.data.response.ResponseType
import com.example.worddefine.network.requestbody.Word
import timber.log.Timber
import com.example.worddefine.database.model.Word as WordModel


class WordEditViewModel(
    app: Application,
    private val wordEditRepository: WordEditRepository) : AndroidViewModel(app) {

    private val _context = getApplication<Application>()
    private val _token = MutableLiveData<String>()


    private val _word = MutableLiveData<WordModel?>()
    val word: LiveData<WordModel?>
        get() = _word

    private val _resultMessage = MutableLiveData<String?>()
    val resultMessage: LiveData<String?>
        get() = _resultMessage

    private val _navigateUp = MutableLiveData<Boolean?>()
    val navigateUp: LiveData<Boolean?>
        get() = _navigateUp


    fun resultMessageDone() {
        _resultMessage.value = null
    }

    fun navigateUpDone() {
        _navigateUp.value = null
    }

    fun bindData(wordId: String){
        wordEditRepository.getWordById(wordId){
            it?.let {
                _word.postValue(it)
            }
        }
    }

    fun onBindDone(){
        _word.value = null
    }

    private fun onDone(methodName: String, responseStatus: ResponseStatus){
        when(responseStatus.type){

            ResponseType.Successful -> {
                _navigateUp.postValue(true)
                Timber.d("$methodName, ${responseStatus.type}")
            }


            ResponseType.RequestInProgress ->
                Timber.d("$methodName, ${responseStatus.type}")

            ResponseType.NotAccess ->
                _resultMessage.postValue(responseStatus.message)

            ResponseType.NotFound ->
                _resultMessage.postValue(responseStatus.message)

            ResponseType.WebServiceError ->
                _resultMessage.postValue(responseStatus.message)

            ResponseType.FailedToConnect ->
                _resultMessage.postValue(_context.getString(R.string.failed_to_connect_to_server))

        }
    }


    private fun addWord(
        name: String,
        definition: String,
        examples: String,
        lang: String,
        password: String?,
        wordListId: String
    ){
        val word = Word(name, wordListId, definition, lang, examples, password)
        wordEditRepository.requestToAdd(_token.value!!, word){
            onDone("addWord", it)
        }
    }

    private fun editWord(
        wordId: String,
        name: String,
        definition: String,
        examples: String,
        lang: String,
        password: String?,
        wordListId: String
    ){
        val word = Word(name, wordListId, definition, lang, examples, password)
        wordEditRepository.requestToEdit(_token.value!!, wordId, word){
            onDone("editWord", it)
        }
    }

    fun onEditButtonClick(
        wordId: String?,
        name: String,
        definition: String,
        examples: String,
        lang: String,
        password: String?,
        wordListId: String) {
        if (wordId == null){
            addWord(name, definition, examples, lang, password, wordListId)
        } else {
            editWord(wordId, name, definition, examples, lang, password, wordListId)
        }
    }

    init {

        _token.value = Token.get(_context)

    }
}
