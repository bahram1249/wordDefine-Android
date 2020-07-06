package com.example.worddefine.data

import com.example.worddefine.data.response.ResponseStatus
import com.example.worddefine.data.response.ResponseType
import com.example.worddefine.database.cache.WordListLocalCache
import com.example.worddefine.database.cache.WordLocalCache
import com.example.worddefine.database.model.Word
import com.example.worddefine.network.requestbody.FavoriteWordList as FavoriteWordListRequestBody
import com.example.worddefine.database.model.WordList
import com.example.worddefine.network.responseproperty.WordListPreviewSingleResponse
import com.example.worddefine.network.WordDefineApiService
import com.example.worddefine.network.WordListsVisibleFilter
import com.example.worddefine.network.responseproperty.WordPreviewSingleResponse
import kotlinx.coroutines.*
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

class WordRepository(
    private val service: WordDefineApiService,
    private val wordLocalCache: WordLocalCache
) {

    private var _lastRequestPage = 1
    private var _isRequestMoreInProgress = false
    private var _isRequestToRemoveInProgress = false


    private val _wordRepositoryJob = Job()
    private val _coroutineScope =
        CoroutineScope(_wordRepositoryJob + Dispatchers.Default)


    fun requestMore(
        token: String,
        wordAccessToken: String,
        limit: Int,
        done: (responseStatus: ResponseStatus) -> Unit
    ) {
        requestMoreAndSave(token, wordAccessToken, limit, done)
    }

    private fun requestMoreAndSave(
        token: String,
        wordAccessToken: String,
        limit: Int,
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

        _coroutineScope.launch {

            try {

                val webResponse = service.getWordsAsync(
                    token,
                    wordAccessToken,
                    limit,
                    _lastRequestPage
                ).await()

                if (webResponse.isSuccessful) {
                    if (webResponse.body()!!.result.size > 0) {

                        // result of api request
                        val wordsResponse = webResponse.body()!!.result.toList();

                        // convert response result to room model
                        val words = wordsResponse.map {
                            wordCreator(it)
                        }

                        // insert to room database
                        wordLocalCache.insertAll(words) {
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

    fun wordsByWordListIdCount(wordListId: String, done: (count: Int)-> Unit){
        _coroutineScope.launch {
            wordLocalCache.wordsByWordListIdCount(wordListId){
                done(it)
            }
        }
    }

/*
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
    }*/

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

    private fun wordCreator(wordPreviewSingleResponse: WordPreviewSingleResponse): Word {
        return Word(
            id =  wordPreviewSingleResponse.id,
            name = wordPreviewSingleResponse.name,
            definition = wordPreviewSingleResponse.definition,
            examples = wordPreviewSingleResponse.examples,
            lang = wordPreviewSingleResponse.lang,
            wordList = wordPreviewSingleResponse.wordList,
            dateCreate = wordPreviewSingleResponse.dateCreate.time,
            username = wordPreviewSingleResponse.user.name,
            userId = wordPreviewSingleResponse.user.id
        )
    }

    fun requestToRemove(
        token: String,
        word: Word,
        done: (responseStatus: ResponseStatus) -> Unit) {

        val responseStatus = ResponseStatus(ResponseType.Successful, null)

        if (_isRequestToRemoveInProgress){
            responseStatus.type = ResponseType.RequestInProgress
            responseStatus.message = null
            done(responseStatus)
            return
        }

        _isRequestToRemoveInProgress = true

        _coroutineScope.launch {

            try {

                val webResponse = service.deleteWordAsync(token, word.id).await()

                if (webResponse.isSuccessful){

                    wordLocalCache.removeById(word.id){
                        // removed
                    }

                    done(responseStatus)
                    _isRequestToRemoveInProgress = false
                }else {

                    // response statusCode != 200
                    val errorMessage: String? =
                        JSONObject(webResponse.errorBody()!!.string()).optString("error")

                    responseStatus.type = ResponseType.WebServiceError
                    responseStatus.message = errorMessage

                    done(responseStatus)
                    _isRequestToRemoveInProgress = false
                }

            }catch (e: IOException){

                responseStatus.type = ResponseType.FailedToConnect
                responseStatus.message = null
                done(responseStatus)
                _isRequestToRemoveInProgress = false
            }
        }
    }

}