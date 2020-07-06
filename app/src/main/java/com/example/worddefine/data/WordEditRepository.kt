package com.example.worddefine.data

import com.example.worddefine.data.response.ResponseStatus
import com.example.worddefine.data.response.ResponseType
import com.example.worddefine.database.cache.WordLocalCache
import com.example.worddefine.network.WordDefineApiService
import com.example.worddefine.network.requestbody.Word
import com.example.worddefine.network.responseproperty.WordResponse
import com.example.worddefine.database.model.Word as WordModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

class WordEditRepository(
    private val service: WordDefineApiService,
    private val wordLocalCache: WordLocalCache
) {

    private var _isRequestToAddInProgress = false
    private var _isRequestToEditInProgress = false

    private val _wordEditRepositoryJob = Job()
    private val _coroutineScope =
        CoroutineScope(_wordEditRepositoryJob + Dispatchers.Default)

    fun requestToAdd(
        token: String,
        word: Word,
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

                val webResponse = service.postWordAsync(token, word).await()

                if (webResponse.isSuccessful){

                    val wordResult = webResponse.body()!!.result
                    wordLocalCache.insert(createWord(wordResult)){
                        // inserted in db
                    }

                    done(responseStatus)
                    _isRequestToAddInProgress = false

                } else {
                    // response statusCode != 200
                    val errorMessage: String? =
                        JSONObject(webResponse.errorBody()!!.string()).optString("error")

                    Timber.d("requestToAdd, $errorMessage")

                    responseStatus.type = ResponseType.WebServiceError
                    responseStatus.message = errorMessage

                    done(responseStatus)
                    _isRequestToAddInProgress = false
                }

            }catch (e: IOException){

                responseStatus.type = ResponseType.FailedToConnect
                responseStatus.message = null;

                done(responseStatus)
                _isRequestToAddInProgress = false

            }
        }
    }

    fun requestToEdit(
        token: String,
        wordId: String,
        word: Word,
        done: (responseStatus: ResponseStatus) -> Unit
    ){

        val responseStatus = ResponseStatus(ResponseType.Successful, null)

        if (_isRequestToEditInProgress){
            responseStatus.type = ResponseType.RequestInProgress
            responseStatus.message = null
            done(responseStatus)
            return
        }

        _isRequestToEditInProgress = true

        _coroutineScope.launch {

            try {

                val webResponse = service.putWordAsync(token, wordId, word).await()

                if (webResponse.isSuccessful){
                    val wordResult = webResponse.body()!!.result
                    wordLocalCache.insert(createWord(wordResult)){
                        // inserted in db
                    }

                    done(responseStatus)
                    _isRequestToAddInProgress = false

                } else {
                    // response statusCode != 200
                    val errorMessage: String? =
                        JSONObject(webResponse.errorBody()!!.string()).optString("error")

                    Timber.d("requestToAdd, $errorMessage")

                    responseStatus.type = ResponseType.WebServiceError
                    responseStatus.message = errorMessage

                    done(responseStatus)
                    _isRequestToAddInProgress = false
                }

            }catch (e: IOException){

                responseStatus.type = ResponseType.FailedToConnect
                responseStatus.message = null;

                done(responseStatus)
                _isRequestToAddInProgress = false
            }

        }

    }

    private fun createWord(word: WordResponse): com.example.worddefine.database.model.Word{
        return WordModel(
            id = word.id,
            name = word.name,
            definition = word.definition,
            examples = word.examples,
            lang = word.lang,
            dateCreate = word.dateCreate.time,
            userId = word.user.id,
            username = word.user.name,
            wordList = word.wordList
        )
    }

    fun getWordById(wordId: String, done: (word: WordModel?) -> Unit){
        wordLocalCache.getWordById(wordId){
            done(it)
        }
    }
}