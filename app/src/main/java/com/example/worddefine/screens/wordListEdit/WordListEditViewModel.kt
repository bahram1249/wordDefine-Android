package com.example.worddefine.screens.wordListEdit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.worddefine.R
import com.example.worddefine.Token
import com.example.worddefine.data.WordListEditRepository
import com.example.worddefine.data.response.ResponseStatus
import com.example.worddefine.data.response.ResponseType
import com.example.worddefine.network.WordListsVisibleFilter
import com.example.worddefine.network.requestbody.WordList
import com.example.worddefine.database.model.WordList as WordListModel
import timber.log.Timber


class WordListEditViewModel(
    app: Application,
    private val wordListEditRepository: WordListEditRepository) : AndroidViewModel(app) {

    private val _context = getApplication<Application>()
    private val _token = MutableLiveData<String>()

    private val _resultMessage = MutableLiveData<String?>()
    val resultMessage: LiveData<String?>
        get() = _resultMessage

    private val _navigateUp = MutableLiveData<Boolean?>()
    val navigateUp: LiveData<Boolean?>
        get() = _navigateUp


    private val _wordList = MutableLiveData<WordListModel?>()
    val wordList: LiveData<WordListModel?>
        get() = _wordList


    fun onBindDone(){
        _wordList.value = null
    }

    private fun addWordList(
        wordListTitle: String,
        visibleFilter: WordListsVisibleFilter,
        addWordBy: WordListsVisibleFilter,
        password: String?){

        val wordList = WordList(wordListTitle, visibleFilter.value, addWordBy.value, password)
        wordListEditRepository.requestToAdd(_token.value!!, wordList){
            onDone("addWordList", it)
        }
    }

    private fun editWordList(
        wordListId: String,
        wordListTitle: String,
        visibleFilter: WordListsVisibleFilter,
        addWordBy: WordListsVisibleFilter,
        password: String?){

        val wordList = WordList(wordListTitle, visibleFilter.value, addWordBy.value, password)
        wordListEditRepository.requestToEdit(_token.value!!, wordListId, wordList){
            onDone("editWordList", it)
        }
    }

    fun bindData(wordListId: String){
        wordListEditRepository.getWordListById(wordListId){
            it?.let {
                _wordList.postValue(it)
            }
        }
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

    fun onEditButtonClick(
        wordListId: String?,
        wordListTitle: String,
        visibleFilter: WordListsVisibleFilter,
        addWordBy: WordListsVisibleFilter,
        password: String?) {

        if (wordListId == null){
            addWordList(wordListTitle, visibleFilter, addWordBy, password)
        } else {
            editWordList(wordListId, wordListTitle, visibleFilter, addWordBy, password)
        }
    }

    fun resultMessageDone() {
        _resultMessage.value = null
    }

    fun navigateUpDone() {
        _navigateUp.value = null
    }


    init {
        _token.value = Token.get(_context)
    }
}
