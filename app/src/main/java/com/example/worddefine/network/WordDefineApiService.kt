package com.example.worddefine.network

import com.example.worddefine.BuildConfig
import com.example.worddefine.network.requestbody.*
import com.example.worddefine.network.requestbody.FavoriteWordList
import com.example.worddefine.network.requestbody.User
import com.example.worddefine.network.responseproperty.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(CustomDateAdapter())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BuildConfig.BASE_URL)
    .build()

enum class WordListsVisibleFilter(var value: String){
    SHOW_EVERYONE("everyone"),
    SHOW_ONLY_ME("onlyMe"),
    SHOW_USER_WITH_PASSWORD("userWithPassword")
}

interface WordDefineApiService {
    @POST("api/auth")
    fun postAuthAsync(@Body auth: Auth):
            Deferred<Response<AuthResponse>>

    @POST("api/users")
    fun postUsersAsync(@Body user: User):
            Deferred<Response<UserResponse>>

    @GET("api/users/me")
    fun getMeAsync(@Header("x-auth-token") xAuthToken: String):
            Deferred<Response<MeDetailsResult>>

    @GET("api/wordLists")
    fun getWordListsAsync(@Header("x-auth-token") xAuthToken: String,
                          @Query("createBy") createBy: String,
                          @Query("limit") limit: Int,
                          @Query("page") page: Int):
            Deferred<Response<WordListsResponse>>

    @GET("api/words")
    fun getWordsAsync(@Header("x-auth-token") xAuthToken: String,
                      @Header("word-access-token") wordAccessToken: String,
                      @Query("limit") limit: Int,
                      @Query("page") page: Int):
            Deferred<Response<WordsResponse>>

    @DELETE("api/wordLists/{id}")
    fun deleteWordListAsync(@Header("x-auth-token") xAuthToken: String,
                            @Path("id") id: String):
            Deferred<Response<WordListSingleResponse>>

    @POST("api/wordLists/token/{id}")
    fun getWordListTokenAsync(@Header("x-auth-token") xAuthToken: String,
                         @Path("id") id: String,
                         @Body wordListPassword: WordListPassword):
            Deferred<Response<WordListTokenResponse>>

    @POST("api/favoriteWordLists")
    fun postFavoriteWordListsAsync(@Header("x-auth-token") xAuthToken: String,
                                   @Body favoriteWordList: FavoriteWordList):
            Deferred<Response<FavoriteWordListResponse>>

    @DELETE("api/favoriteWordLists/{id}")
    fun deleteFavoriteWordListsAsync(@Header("x-auth-token") xAuthToken: String,
                                   @Path("id") id: String):
            Deferred<Response<FavoriteWordListResponse>>

    @GET("api/favoriteWordLists")
    fun getFavoriteWordListsAsync(@Header("x-auth-token") xAuthToken: String,
                                  @Query("limit") limit: Int,
                                  @Query("page") page: Int):
            Deferred<Response<FavoriteWordListsResponse>>


    @POST("api/wordLists")
    fun postWordListAsync(@Header("x-auth-token") xAuthToken: String,
                                   @Body wordList: WordList):
            Deferred<Response<WordListResponseResult>>

    @PUT("api/wordLists/{id}")
    fun putWordListAsync(@Header("x-auth-token") xAuthToken: String,
                         @Path("id") wordListId: String,
                         @Body wordList: WordList):
            Deferred<Response<WordListResponseResult>>


    @POST("api/words")
    fun postWordAsync(@Header("x-auth-token") xAuthToken: String,
                      @Body word: Word):
            Deferred<Response<WordResponseResult>>

    @PUT("api/words/{id}")
    fun putWordAsync(@Header("x-auth-token") xAuthToken: String,
                     @Path("id") wordId: String,
                    @Body word:Word):
            Deferred<Response<WordResponseResult>>

    @DELETE("api/words/{id}")
    fun deleteWordAsync(@Header("x-auth-token") xAuthToken: String,
                        @Path("id") wordId: String):
            Deferred<Response<WordDeleteResponseResult>>

    @Multipart
    @POST("api/users/profilePhoto")
    fun uploadProfileImageAsync(@Header("x-auth-token") xAuthToken: String,
                                @Part avatar: MultipartBody.Part):
            Deferred<Response<UploadResponseResult>>

}

object WordDefineApi {
    val retrofitService : WordDefineApiService by lazy {
        retrofit.create(WordDefineApiService::class.java)
    }
}