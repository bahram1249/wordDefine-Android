package com.example.worddefine.screens.favorites

import android.app.Application
import android.content.Intent
import android.util.Log
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
import com.example.worddefine.data.FavoriteWordListRepository
import com.example.worddefine.data.WordListRepository
import com.example.worddefine.data.response.ResponseStatus
import com.example.worddefine.data.response.ResponseType
import com.example.worddefine.database.WordDefineDatabase
import com.example.worddefine.database.model.WordList
import com.example.worddefine.database.model.WordListToken
import com.example.worddefine.network.WordListsVisibleFilter
import com.example.worddefine.network.requestbody.WordListPassword
import timber.log.Timber

class FavoritesViewModel(app: Application,
                         private val favoriteWordListRepository: FavoriteWordListRepository):
AndroidViewModel(app) {

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

    private val _wordLists = MutableLiveData<LiveData<PagedList<WordList>>>()
    val wordLists: LiveData<LiveData<PagedList<WordList>>>
        get() = _wordLists

    private val _isRequestInProgress = MutableLiveData<Boolean>()
    val isRequestInProgress: LiveData<Boolean>
        get() = _isRequestInProgress

    private val _token = MutableLiveData<String>()
    private val _userId = MutableLiveData<String>()

    private val _resultMessage = MutableLiveData<String?>()
    val resultMessage: LiveData<String?>
        get() = _resultMessage

    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    private val _navigateToWords = MutableLiveData<WordListToken>()
    val navigateToWords: LiveData<WordListToken>
        get() = _navigateToWords


    private val _navigateToWordListPassword = MutableLiveData<WordList>()
    val navigateToWordListPassword: LiveData<WordList>
        get() = _navigateToWordListPassword


    fun resultMessageDone(){
        _resultMessage.value = null
    }

    fun snackMessageDone(){
        _snackMessage.value = null
    }


    private fun deleteFavoriteList(wordList: WordList){
        favoriteWordListRepository.requestToDeleteFavorite(_token.value!!, wordList) {
            onDone("deleteFavoriteList", it)
        }
    }

    private fun addFavoriteWordList(wordList: WordList){
        favoriteWordListRepository.requestToAddFavorite(_token.value!!, wordList) {
            onDone("requestToAddFavorite", it)
        }
    }

    fun shareIntent(wordList: WordList): Intent {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "${wordList.title} : ${BuildConfig.BASE_URL}/api/wordLists/${wordList.id}"
            )
            type = "text/plain"
        }

        return Intent.createChooser(sendIntent, null)
    }

    fun onDeleteWordListProperty(wordList: WordList){
        favoriteWordListRepository.requestToRemove(_token.value!!, wordList){
            onDone("requestToRemove", it)
        }
    }

    fun onFavoriteClick(wordList: WordList) {
        if(wordList.favoriteWordList != null){
            deleteFavoriteList(wordList)
        } else {
            addFavoriteWordList(wordList)
        }
    }

    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {

        if(visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {

            requestMore()

        }
    }

    private fun requestMore(){

        if (_isRequestInProgress.value == true) return

        _isRequestInProgress.value = true
        favoriteWordListRepository.requestMore(
            _token.value!!,
            LIMIT_WEBSERVICE
        ){

            onDone("requestMore", it)

            _isRequestInProgress.postValue(false)
        }
    }

    private fun onDone(methodName: String, responseStatus: ResponseStatus){
        when(responseStatus.type){

            ResponseType.Successful ->
                Timber.d("$methodName, ${responseStatus.type}")

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

    fun onNavigateToWordsDone(){
        _navigateToWords.value = null
    }

    fun onNavigateToWordListPasswordDone(){
        _navigateToWordListPassword.value = null
    }

    private fun requestWordListToken(wordList: WordList, password: String?){
        val wordListPassword = WordListPassword(password)
        favoriteWordListRepository.requestToGetWordListToken(_token.value!!,wordList, wordListPassword){
            onRequestWordListTokenDone(it, wordList)
        }
    }

    private fun navigateToWords(wordListToken: WordListToken?){
        if (wordListToken == null) return
        _navigateToWords.postValue(wordListToken)
    }

    private fun onRequestWordListTokenDone(responseStatus: ResponseStatus, wordList:WordList){
        when(responseStatus.type){

            ResponseType.Successful ->
            {
                Timber.d("${responseStatus.type}")
                val wordListToken = _database.wordListTokenDao().getTokenByWordListId(wordList.id)
                navigateToWords(wordListToken)
            }


            ResponseType.RequestInProgress ->
                Timber.d("${responseStatus.type}")

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

    fun onWordListClick(wordList: WordList) {
        favoriteWordListRepository.getWordListTokenAsync(wordList){
            if(it != null) navigateToWords(it)

            else if (wordList.userId == _userId.value){
                requestWordListToken(wordList, null)
            }

            else if (wordList.visible == WordListsVisibleFilter.SHOW_EVERYONE.value){
                requestWordListToken(wordList, null)
            }
            else if (wordList.visible == WordListsVisibleFilter.SHOW_USER_WITH_PASSWORD.value){
                _navigateToWordListPassword.postValue(wordList)
            }
        }
    }

    init {
        // get current user token
        _token.value = Token.get(_context)
        // get current userId
        _userId.value = Token.getUserId(_context)

        _wordLists.value =
            _database.wordListDao().wordListsIsFavorite().toLiveData(pagingConfig)


        requestMore()
    }
}
