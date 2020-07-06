package com.example.worddefine.network.responseproperty

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*


data class WordListResponseResult(
    val result: WordListResponse
)
@JsonClass(generateAdapter = true)
data class WordListResponse (
    @Json(name = "_id") val id: String,
    val title: String,
    val visible: String,
    val addWordBy: String,
    val user: UserPreviewResponse,
    val dateCreate: Date
)