package com.example.worddefine.network.responseproperty

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class FavoriteWordListsResponse (
    val result: MutableList<FavoriteWordListSingleResponse>,
    val error: String?
)

@JsonClass(generateAdapter = true)
data class FavoriteWordListSingleResponse (
    @Json(name = "_id") val id: String,
    val wordList: WordListPreviewInFavoriteSingleResponse,
    val user: String,
    val dateSubmitted: Date?
)