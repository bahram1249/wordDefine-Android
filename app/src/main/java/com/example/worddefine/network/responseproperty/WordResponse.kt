package com.example.worddefine.network.responseproperty

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class WordResponseResult (
    var result: WordResponse
)

@JsonClass(generateAdapter = true)
data class WordDeleteResponseResult (
    var result: WordDeleteResponse
)

@JsonClass(generateAdapter = true)
data class WordDeleteResponse (
    @Json(name = "_id") val id: String,
    val name: String,
    val definition: String,
    val examples: String,
    val lang: String,
    val wordList: String,
    val user: String,
    val dateCreate: Date
)

@JsonClass(generateAdapter = true)
data class WordResponse (
    @Json(name = "_id") val id: String,
    val name: String,
    val definition: String,
    val examples: String,
    val lang: String,
    val wordList: String,
    val user: UserPreviewResponse,
    val dateCreate: Date
)