package com.example.worddefine.network.responseproperty

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class WordsResponse (
    var result: MutableList<WordPreviewSingleResponse>,
    var previousLink: String?,
    var nextLink: String?
)

@JsonClass(generateAdapter = true)
data class WordsSingleResponse (
    var result: WordsSingle
)

// for deleting
@JsonClass(generateAdapter = true)
data class WordsSingle(
    @Json(name = "_id") val id: String,
    val name: String,
    val definition: String,
    val lang: String,
    val user: String,
    val wordList: String,
    val dateCreate: Date
)

@JsonClass(generateAdapter = true)
data class WordPreviewSingleResponse(
    @Json(name = "_id") val id: String,
    val name: String,
    val definition: String,
    val examples: String,
    val lang: String,
    val user: UserPreviewResponse,
    val wordList: String,
    val dateCreate: Date
)