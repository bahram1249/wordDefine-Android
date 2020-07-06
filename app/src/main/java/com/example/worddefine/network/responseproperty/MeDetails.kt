package com.example.worddefine.network.responseproperty

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class MeDetailsResult (
    val result: MeDetails
)

data class MeDetails (
    @Json(name = "_id") val id: String,
    val name: String,
    val email: String,
    val dateCreate: Date
)