package com.example.worddefine.data

import com.example.worddefine.data.response.ResponseStatus
import com.example.worddefine.data.response.ResponseType
import com.example.worddefine.database.cache.WordListLocalCache
import com.example.worddefine.network.WordDefineApiService
import com.example.worddefine.network.requestbody.WordList
import com.example.worddefine.network.responseproperty.WordListResponse
import com.example.worddefine.database.model.WordList as WordListModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException


class WordListEditRepository(
    private val service: WordDefineApiService,
    private val wordListLocalCache: WordListLocalCache) {

    private var _isRequestToAddInProgress = false
    private var _isRequestToEditInProgress = false

    private val _wordListEditRepositoryJob = Job()
    private val _coroutineScope =
        CoroutineScope(_wordListEditRepositoryJob + Dispatchers.Default)

    fun requestToAdd(
        token: String,
        wordList: WordList,
        done: (responseStatus: ResponseStatus) -> Unit
    ){

        val responseStatus = ResponseStatus(ResponseType.Successful, null)

        if (_isRequestToAddInProgress){
            responseStatus.type = ResponseType.RequestInProgress
            responseStatus.message = null
            done(responseStatus)
            return
        }

        _isRequestToAddInProgress = true

        _coroutineScope.launch {

            try {
                val webResponse = service.postWordListAsync(token, wordList).await()

                if (webResponse.isSuccessful){
                    val wordListResult = webResponse.body()!!.result
                    wordListLocalCache.insert(wordListCreator(wordListResult)){
                        // inserted in db
                    }

                    done(responseStatus)
                    _isRequestToAddInProgress = false

                }
                else {
                    // response statusCode != 200
                    val errorMessage: String? =
                        JSONObject(webResponse.errorBody()!!.string()).optString("error")

                    Timber.d("requestToAdd, $errorMessage")

                    responseStatus.type = ResponseType.WebServiceError
                    responseStatus.message = errorMessage

                    done(responseStatus)
                    _isRequestToAddInProgress = false
                }
            }
            catch (e: IOException) {
                responseStatus.type = ResponseType.FailedToConnect
                responseStatus.message = null;

                done(responseStatus)
                _isRequestToAddInProgress = false
            }

        }

    }

    fun requestToEdit(
        token: String,
        wordListId: String,
        wordList: WordList,
        done: (responseStatus: ResponseStatus) -> Unit
    ){

        val responseStatus = ResponseStatus(ResponseType.Successful, null)

        if (_isRequestToEditInProgress){
            responseStatus.type = ResponseType.RequestInProgress
            responseStatus.message = null
            done(responseStatus)
        }

        _isRequestToEditInProgress = false

        _coroutineScope.launch {
            try {
                val webResponse = service.putWordListAsync(token, wordListId, wordList).await()

                if (webResponse.isSuccessful){
                    val wordListResult = webResponse.body()!!.result
                    wordListLocalCache.insert(wordListCreator(wordListResult)){
                        // inserted in db
                    }

                    done(responseStatus)
                    _isRequestToEditInProgress = false

                }
                else {
                    // response statusCode != 200
                    val errorMessage: String? =
                        JSONObject(webResponse.errorBody()!!.string()).optString("error")

                    Timber.d("requestToAdd, $errorMessage")

                    responseStatus.type = ResponseType.WebServiceError
                    responseStatus.message = errorMessage

                    done(responseStatus)
                    _isRequestToEditInProgress = false
                }
            }
            catch (e: IOException) {
                responseStatus.type = ResponseType.FailedToConnect
                responseStatus.message = null;

                done(responseStatus)
                _isRequestToEditInProgress = false
            }
        }
    }

    fun getWordListById( wordListId: String, done: (wordList: WordListModel?) -> Unit){
        wordListLocalCache.getWordListById(wordListId){
            done(it)
        }
    }

    private fun wordListCreator(wordListResult: WordListResponse): WordListModel {
        return WordListModel(
            id = wordListResult.id,
            title = wordListResult.title,
            visible = wordListResult.visible,
            addWordBy = wordListResult.addWordBy,
            userId = wordListResult.user.id,
            username = wordListResult.user.name,
            dateCreate = wordListResult.dateCreate.time,
            favoriteWordList = null,
            favoriteDateSubmitted = null
        )
    }
}