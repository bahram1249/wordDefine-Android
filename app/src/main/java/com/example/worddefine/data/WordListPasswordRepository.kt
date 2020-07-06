package com.example.worddefine.data

import com.example.worddefine.data.response.ResponseStatus
import com.example.worddefine.data.response.ResponseType
import com.example.worddefine.database.cache.WordListTokenLocalCache
import com.example.worddefine.database.model.WordList
import com.example.worddefine.network.WordDefineApiService
import com.example.worddefine.network.requestbody.WordListPassword
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.IOException

class WordListPasswordRepository(
    private val service: WordDefineApiService,
    private val wordListTokenLocalCache: WordListTokenLocalCache
) {

    private var _isRequestPending: Boolean = false
    private val _wordListPasswordRepositoryJob = Job()
    private val __coroutineScope =
        CoroutineScope(_wordListPasswordRepositoryJob + Dispatchers.Default)

    fun requestToGetWordListToken(
        token: String,
        wordListId: String,
        wordListTitle: String,
        wordListPassword: String,
        done: (responseStatus: ResponseStatus) -> Unit){

        val responseStatus = ResponseStatus(ResponseType.Successful, null)


        if (_isRequestPending) {
            responseStatus.type = ResponseType.RequestInProgress
            done(responseStatus)
            return
        }

        _isRequestPending = true

        __coroutineScope.launch {

            try {

                val webResponse =
                    service.getWordListTokenAsync(
                            token,
                            wordListId,
                            WordListPassword(wordListPassword)).await()

                if (webResponse.isSuccessful){
                    val wordListToken = webResponse.body()!!.result
                    wordListTokenLocalCache.insert(wordListId, wordListTitle, wordListToken){
                        _isRequestPending = false
                        done(responseStatus)
                    }
                }else{
                    val errorMessage: String? =
                        JSONObject(webResponse.errorBody()!!.string()).optString("error")

                    if (webResponse.code() == 403){
                        responseStatus.type = ResponseType.NotAccess
                        responseStatus.message = errorMessage
                    }
                    else {
                        responseStatus.type = ResponseType.WebServiceError
                        responseStatus.message = errorMessage
                    }
                    done(responseStatus)
                    _isRequestPending = false
                }

            }catch (e: IOException){
                responseStatus.type = ResponseType.NotAccess
                responseStatus.message = null
                _isRequestPending = false
                done(responseStatus)
            }
        }
    }
}