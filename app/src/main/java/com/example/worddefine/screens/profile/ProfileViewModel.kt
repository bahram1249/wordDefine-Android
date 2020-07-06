package com.example.worddefine.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.worddefine.Token
import com.example.worddefine.network.WordDefineApi
import com.example.worddefine.network.responseproperty.MeDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.http.Multipart
import timber.log.Timber
import java.io.IOException


class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val _viewModelJob = Job()
    private val _coroutineScope =
        CoroutineScope(_viewModelJob + Dispatchers.Default)

    private val _context = getApplication<Application>()
    private val _token = MutableLiveData<String>()

    private val _userDetails = MutableLiveData<MeDetails?>()
    val userDetails: LiveData<MeDetails?>
        get() = _userDetails

    private val _refreshProfilePhoto = MutableLiveData<Boolean?>()
    val refreshProfilePhoto: LiveData<Boolean?>
        get() = _refreshProfilePhoto

    fun refreshProfilePhotoDone(){
        _refreshProfilePhoto.value = null
    }

    private fun requestProfileDetails(){
        _coroutineScope.launch {
            try {
                val webResponse = WordDefineApi.retrofitService.getMeAsync(_token.value!!).await()
                if (webResponse.isSuccessful){
                    val result = webResponse.body()!!.result
                    _userDetails.postValue(result)
                }
            }
            catch (e: IOException){

            }


        }
    }

    fun uploadPicture(requestBody: RequestBody) {
        val avatar =
            MultipartBody.Part.createFormData("avatar", "avatar.jpg", requestBody)
        requestToUploadPhoto(avatar)
    }



    private fun requestToUploadPhoto(avatar: MultipartBody.Part){
        _coroutineScope.launch {
            try {
                val webResponse = WordDefineApi.retrofitService
                                        .uploadProfileImageAsync(_token.value!!, avatar).await()
                if (webResponse.isSuccessful){
                    Timber.d("upload image successfully")
                    _refreshProfilePhoto.postValue(true)
                }else {
                    val errorMessage: String? =
                        JSONObject(webResponse.errorBody()!!.string()).optString("error")
                    Timber.d("failed to upload image, the status code is not ok ...")
                    Timber.d("errorMessage ${errorMessage}")
                }
            } catch (e: IOException){
                Timber.d("failed upload image by network ...")
            }
        }
    }


    init {
        _token.value = Token.get(_context)
        requestProfileDetails()
    }
}
