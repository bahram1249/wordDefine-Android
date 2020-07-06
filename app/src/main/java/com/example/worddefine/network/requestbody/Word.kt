package com.example.worddefine.network.requestbody

data class Word(
    val name: String,
    val wordList: String,
    val definition: String?,
    val lang: String?,
    val examples: String?,
    val password: String?
)