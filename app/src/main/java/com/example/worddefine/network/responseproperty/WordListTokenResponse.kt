package com.example.worddefine.network.responseproperty

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class WordListTokenResponse (
    val result: String
)