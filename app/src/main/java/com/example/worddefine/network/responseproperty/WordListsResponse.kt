package com.example.worddefine.network.responseproperty

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*


// need for preview
@JsonClass(generateAdapter = true)
data class FavoriteWordList(
    @Json(name = "_id") var id: String,
    var wordList: String,
    var user: String?,
    var dateSubmitted: Date?
)

@JsonClass(generateAdapter = true)
data class WordListsResponse (
    var result: MutableList<WordListPreviewSingleResponse>,
    var previousLink: String?,
    var nextLink: String?
)

@JsonClass(generateAdapter = true)
data class WordListSingleResponse (
    var result: WordListSingle
)

// for deleting
@JsonClass(generateAdapter = true)
data class WordListSingle(
    @Json(name = "_id") val id: String,
    val title: String,
    val visible: String,
    val addWordBy: String,
    val user: String,
    val dateCreate: Date
)

@JsonClass(generateAdapter = true)
data class WordListPreviewSingleResponse(
    @Json(name = "_id") val id: String,
    val title: String,
    val visible: String,
    val addWordBy: String,
    val user: UserPreviewResponse,
    val favoriteWordList: FavoriteWordList?,
    val dateCreate: Date
)

@JsonClass(generateAdapter = true)
data class WordListPreviewInFavoriteSingleResponse(
    @Json(name = "_id") val id: String,
    val title: String,
    val visible: String,
    val addWordBy: String,
    val user: UserPreviewResponse,
    val dateCreate: Date
)

@JsonClass(generateAdapter = true)
data class UserPreviewResponse(
    @Json(name = "_id") val id: String,
    val name: String
)