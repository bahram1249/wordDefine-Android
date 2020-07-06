package com.example.worddefine.data

import com.example.worddefine.data.response.ResponseStatus
import com.example.worddefine.data.response.ResponseType
import com.example.worddefine.database.cache.WordListLocalCache
import com.example.worddefine.database.cache.WordListTokenLocalCache
import com.example.worddefine.database.dao.WordListTokenDao
import com.example.worddefine.network.requestbody.FavoriteWordList as FavoriteWordListRequestBody
import com.example.worddefine.database.model.WordList
import com.example.worddefine.database.model.WordListToken
import com.example.worddefine.network.responseproperty.WordListPreviewSingleResponse
import com.example.worddefine.network.WordDefineApiService
import com.example.worddefine.network.WordListsVisibleFilter
import com.example.worddefine.network.requestbody.WordListPassword
import kotlinx.coroutines.*
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

class WordListRepository(
    private val service: WordDefineApiService,
    private val wordListLocalCache: WordListLocalCache,
    private val wordListTokenLocalCache: WordListTokenLocalCache
) {

    private var _lastRequestPage = 1
    private var _isRequestMoreInProgress = false
    private var _isRequestToRemoveInProgress = false
    private var _isRequestToGetTokenInProgress = false

    private var _visibleFilter = WordListsVisibleFilter.SHOW_EVERYONE

    private val _wordListRepositoryJob = Job()
    private val _coroutineScope =
        CoroutineScope(_wordListRepositoryJob + Dispatchers.Default)


    fun requestMore(
        token: String, visibleFilter: WordListsVisibleFilter, limit: Int,
        done: (responseStatus: ResponseStatus) -> Unit
    ) {
        requestMoreAndSave(token, visibleFilter, limit, done)
    }

    private fun requestMoreAndSave(
        token: String, visibleFilter: WordListsVisibleFilter, limit: Int,
        done: (responseStatus: ResponseStatus) -> Unit
    ) {

        val responseStatus = ResponseStatus(ResponseType.Successful, null)

        if (_isRequestMoreInProgress) {
            responseStatus.type = ResponseType.RequestInProgress
            responseStatus.message = null
            done(responseStatus)
            return
        }

        _isRequestMoreInProgress = true

        if (visibleFilter !== _visibleFilter) {
            _visibleFilter = visibleFilter
            _lastRequestPage = 1
        }

        _coroutineScope.launch {

            try {

                val webResponse = service.getWordListsAsync(
                    token,
                    visibleFilter.value,
                    limit,
                    _lastRequestPage
                ).await()

                if (webResponse.isSuccessful) {
                    if (webResponse.body()!!.result.size > 0) {

                        // result of api request
                        val wordListsResponse = webResponse.body()!!.result.toList();

                        // convert response result to room model
                        val wordLists = wordListsResponse.map {
                            wordListCreator(it)
                        }

                        // insert to room database
                        wordListLocalCache.insertAll(wordLists) {
                            _lastRequestPage++
                        }

                    }

                    done(responseStatus)
                    _isRequestMoreInProgress = false

                } else {
                    // response statusCode != 200
                    val errorMessage: String? =
                        JSONObject(webResponse.errorBody()!!.string()).optString("error")

                    Timber.d("requestSaveAndMore, $errorMessage")

                    responseStatus.type = ResponseType.WebServiceError
                    responseStatus.message = errorMessage

                    done(responseStatus)
                    _isRequestMoreInProgress = false
                }

            } catch (e: IOException) {

                responseStatus.type = ResponseType.FailedToConnect
                responseStatus.message = null;

                done(responseStatus)
                _isRequestMoreInProgress = false
            }

        }
    }

    fun requestToRemove(
        token: String, wordList: WordList,
        done: (responseStatus: ResponseStatus) -> Unit
    ) {

        val responseStatus = ResponseStatus(ResponseType.Successful, null)

        if (_isRequestToRemoveInProgress) {
            responseStatus.type = ResponseType.RequestInProgress
            responseStatus.message = null
            done(responseStatus)
            return
        }

        _isRequestToRemoveInProgress = true

        _coroutineScope.launch {

            try {

                val webResponse = service.deleteWordListAsync(
                    token,
                    wordList.id
                ).await()

                if (webResponse.isSuccessful) {

                    wordListLocalCache.remove(wordList) {

                        Timber.d("wordList: ${wordList.id} removed")

                    }

                    done(responseStatus)
                    _isRequestToRemoveInProgress = false

                } else {
                    // response statusCode != 200
                    val errorMessage: String? =
                        JSONObject(webResponse.errorBody()!!.string()).optString("error")

                    Timber.d("requestToRemove, $errorMessage")

                    if (webResponse.code() == 404) {
                        wordListLocalCache.remove(wordList) {
                            Timber.d(
                                "wordList: ${wordList.id} removed"
                            )
                        }

                        responseStatus.type = ResponseType.NotFound
                        responseStatus.message = errorMessage

                    } else if (webResponse.code() == 403) {
                        responseStatus.type = ResponseType.NotAccess
                        responseStatus.message = errorMessage
                    } else {
                        responseStatus.type = ResponseType.WebServiceError
                        responseStatus.message = errorMessage
                    }

                    done(responseStatus)
                    _isRequestToRemoveInProgress = false
                }

            } catch (e: IOException) {

                responseStatus.type = ResponseType.FailedToConnect
                responseStatus.message = null;

                done(responseStatus)
                _isRequestToRemoveInProgress = false
            }
        }
    }

    fun requestToAddFavorite(
        token: String, wordList: WordList,
        done: (responseStatus: ResponseStatus) -> Unit
    ) {

        val responseStatus = ResponseStatus(ResponseType.Successful, null)

        _coroutineScope.launch {

            try {

                val webResponse = service.postFavoriteWordListsAsync(
                    token,
                    FavoriteWordListRequestBody(wordList.id)
                ).await()

                if (webResponse.isSuccessful) {
                    wordListLocalCache.addFavoriteWordList(wordList, webResponse.body()!!.result) {

                        Timber.d("favoriteWordListAdd")

                    }
                    done(responseStatus)
                } else {
                    // response statusCode != 200
                    val errorMessage: String? =
                        JSONObject(webResponse.errorBody()!!.string()).optString("error")

                    Timber.d("requestToAddFavoriteWordList, $errorMessage")

                    // something is here need

                    responseStatus.type = ResponseType.WebServiceError
                    responseStatus.message = errorMessage
                }

                done(responseStatus)

            } catch (e: IOException) {
                // response statusCode != 200
                responseStatus.type = ResponseType.FailedToConnect
                responseStatus.message = null

                done(responseStatus)
            }
        }
    }

    fun requestToDeleteFavorite(
        token: String, wordList: WordList,
        done: (responseStatus: ResponseStatus) -> Unit
    ) {

        val responseStatus = ResponseStatus(ResponseType.Successful, null)

        _coroutineScope.launch {

            try {

                val webResponse = service.deleteFavoriteWordListsAsync(
                    token,
                    wordList.favoriteWordList!!
                ).await()

                if (webResponse.isSuccessful) {
                    wordListLocalCache.removeFavoriteWordList(wordList) {
                        Timber.d(
                            "favoriteWordList deleted from wordList: ${wordList.id}"
                        )

                    }
                    done(responseStatus)
                } else {
                    // response statusCode != 200
                    val errorMessage: String? =
                        JSONObject(webResponse.errorBody()!!.string()).optString("error")

                    Timber.d("requestToDeleteFavoriteWordList, $errorMessage")

                    if (webResponse.code() == 404) {
                        wordListLocalCache.removeFavoriteWordList(wordList) {
                            Timber.d(
                                "favoriteWordList of wordList ${wordList.id} removed"
                            )
                        }

                        responseStatus.type = ResponseType.NotFound
                        responseStatus.message = errorMessage

                    } else {
                        responseStatus.type = ResponseType.WebServiceError
                        responseStatus.message = errorMessage
                    }

                    done(responseStatus)
                }
            } catch (e: IOException) {

                responseStatus.type = ResponseType.FailedToConnect
                responseStatus.message = null

                done(responseStatus)
            }
        }
    }

    fun requestToGetWordListToken(
        token: String,
        wordList: WordList,
        wordListPassword: WordListPassword,
        done: (responseStatus: ResponseStatus) -> Unit) {

        val responseStatus = ResponseStatus(ResponseType.Successful, null)

        if (_isRequestToGetTokenInProgress) {
            responseStatus.type = ResponseType.RequestInProgress
            responseStatus.message = null
            return
        }

        _isRequestToGetTokenInProgress = true

        _coroutineScope.launch {

            try {
                val webResponse =
                    service.getWordListTokenAsync(token, wordList.id, wordListPassword).await()

                if(webResponse.isSuccessful){
                    val wordListToken = webResponse.body()!!.result
                    wordListTokenLocalCache.insert(wordList.id, wordList.title, wordListToken){
                        Timber.d("wordList token added to database !")
                    }

                    done(responseStatus)

                    _isRequestToGetTokenInProgress = false
                }
                else {
                    if (webResponse.code() == 403){

                        val errorMessage: String? =
                            JSONObject(webResponse.errorBody()!!.string()).optString("error")

                        responseStatus.type = ResponseType.NotAccess
                        responseStatus.message = errorMessage
                        done(responseStatus)
                    }
                    _isRequestToGetTokenInProgress = false
                }
            }
            catch (e: IOException){
                responseStatus.type = ResponseType.FailedToConnect
                responseStatus.message = null
                done(responseStatus)

                _isRequestToGetTokenInProgress = false
            }
        }
    }

    fun getWordListTokenAsync(wordList: WordList, done: (wordListToken: WordListToken?)-> Unit){
        _coroutineScope.launch {
            val wordListToken = wordListTokenLocalCache.getWordListToken(wordList){
                done(it)
            }
        }
    }
    private fun wordListCreator(wordListPreviewSingleResponse: WordListPreviewSingleResponse): WordList {
        return WordList(
            id = wordListPreviewSingleResponse.id,
            title = wordListPreviewSingleResponse.title,
            visible = wordListPreviewSingleResponse.visible,
            addWordBy = wordListPreviewSingleResponse.addWordBy,
            userId = wordListPreviewSingleResponse.user.id,
            username = wordListPreviewSingleResponse.user.name,
            dateCreate = wordListPreviewSingleResponse.dateCreate.time,
            favoriteWordList = wordListPreviewSingleResponse.favoriteWordList?.id,
            favoriteDateSubmitted = wordListPreviewSingleResponse.favoriteWordList?.dateSubmitted?.time
        )
    }



}