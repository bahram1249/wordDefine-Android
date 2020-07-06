package com.example.worddefine.network.responseproperty

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true) data class AuthResult(
    val token: String,
    val userId: String
)

@JsonClass(generateAdapter = true)
data class AuthResponse (
    @Json(name = "result") val result: AuthResult
)