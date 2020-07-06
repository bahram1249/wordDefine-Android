package com.example.worddefine.screens.words

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.worddefine.BuildConfig
import com.example.worddefine.R
import com.example.worddefine.Token
import com.example.worddefine.data.WordRepository
import com.example.worddefine.data.response.ResponseStatus
import com.example.worddefine.data.response.ResponseType
import com.example.worddefine.database.WordDefineDatabase
import com.example.worddefine.database.model.Word
import com.example.worddefine.network.WordListsVisibleFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber


class WordsViewModel(
    app: Application,
    private val wordRepository: WordRepository,
    private val wordListId: String,
    private val wordAccessToken: String) : AndroidViewModel(app) {

    companion object {
        const val VISIBLE_THRESHOLD = 5
        private const val LIMIT_WEBSERVICE = 50
        private const val LIMIT_DATABASE = 50
    }

    private val pagingConfig = Config(
        pageSize = LIMIT_DATABASE,
        prefetchDistance = LIMIT_DATABASE * 3,
        enablePlaceholders = true
    )

    private val _context = getApplication<Application>()
    private val _database: WordDefineDatabase = WordDefineDatabase.getInstance(_context)!!

    private val _words = MutableLiveData<LiveData<PagedList<Word>>>()
    val words: LiveData<LiveData<PagedList<Word>>>
        get() = _words

    private val _isRequestInProgress = MutableLiveData<Boolean>()

    private val _token = MutableLiveData<String>()
    private val _userId = MutableLiveData<String>()

    private val _resultMessage = MutableLiveData<String?>()
    val resultMessage: LiveData<String?>
        get() = _resultMessage

    private val _wordsViewModelJob = Job()
    private val _coroutineScope =
        CoroutineScope(_wordsViewModelJob + Dispatchers.Default)

    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage


    private val _navigateToWord = MutableLiveData<Word?>()
    val navigateToWord: LiveData<Word?>
        get() = _navigateToWord

    private val _navigateToWordEdit = MutableLiveData<Word?>()
    val navigateToWordEdit: LiveData<Word?>
        get() = _navigateToWordEdit

    private val _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean>
        get() = _status

    private val _wordListOwner = MutableLiveData<String>()
    val wordListOwner: LiveData<String?>
        get() = _wordListOwner

    private val _accessToAddWord = MutableLiveData<Boolean?>()
    val accessToAddWord: LiveData<Boolean?>
        get() = _accessToAddWord

    fun resultMessageDone(){
        _resultMessage.value = null
    }

    fun snackMessageDone(){
        _snackMessage.value = null
    }

    private val _visiblePassword = MutableLiveData<Boolean?>()
    val visiblePassword: LiveData<Boolean?>
        get() = _visiblePassword

    fun visiblePasswordAssigned(){
        _visiblePassword.value = null
    }




    fun navigateToWordDone(){
        _navigateToWord.value = null
    }

    fun navigateToWordEditDone(){
        _navigateToWordEdit.value = null
    }

    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {

        if(visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {

            requestMore()

        }
    }

    private fun requestMore(){

        if (_isRequestInProgress.value == true) return

        _isRequestInProgress.value = true
        wordRepository.requestMore(
            _token.value!!,
            wordAccessToken,
            LIMIT_WEBSERVICE
        ){

            onDone("requestMore", it)

            _isRequestInProgress.postValue(false)
        }
    }

    fun statusChanged(){
        wordRepository.wordsByWordListIdCount(wordListId){
            if (it == 0)
                _status.postValue(false)
            else
                _status.postValue(true)
        }
    }

    private fun onDone(methodName: String, responseStatus: ResponseStatus){
        when(responseStatus.type){

            ResponseType.Successful -> {
                Timber.d("$methodName, ${responseStatus.type}")

                statusChanged()

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
                _snackMessage.postValue(_context.getString(R.string.failed_to_connect_to_server))

        }
    }

    fun onWordItemClick(word: Word) {
        _navigateToWord.value = word
    }

    fun onWordEditClick(word: Word) {
        _navigateToWordEdit.value = word
    }

    fun shareIntent(word: Word): Intent{
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT,
                "${word.name} : ${BuildConfig.BASE_URL}/api/words/${word.id}"
            )
            type = "text/plain"
        }

        return Intent.createChooser(sendIntent, null)
    }

    fun onWordDelete(word: Word) {
        wordRepository.requestToRemove(_token.value!!, word){
            onDone("onWordDelete", it)
        }
    }

    init {
        // get current user token
        _token.value = Token.get(_context)
        // get current userId
        _userId.value = Token.getUserId(_context)

        _words.value =
            _database.wordDao().wordsByWordListId(wordListId).toLiveData(pagingConfig)

        _coroutineScope.launch {
            val wordList = _database.wordListDao().getWordListById(wordListId)
            wordList?.let {

                _wordListOwner.postValue(it.userId)

                // who can add word on this word list ?
                if (it.addWordBy != WordListsVisibleFilter.SHOW_ONLY_ME.value ||
                    it.userId == _userId.value){
                    _accessToAddWord.postValue(true)
                } else {
                    _accessToAddWord.postValue(false)
                }

                // when navigate to edit word, it's necessary to show password field ?
                if (it.addWordBy == WordListsVisibleFilter.SHOW_USER_WITH_PASSWORD.value){
                    _visiblePassword.postValue(true)
                } else {
                    _visiblePassword.postValue(false)
                }

            }
        }

        statusChanged()


        _isRequestInProgress.value = false

        requestMore()
    }

    override fun onCleared() {
        super.onCleared()
    }
}
