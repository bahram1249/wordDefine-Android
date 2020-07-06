package com.example.worddefine.network.requestbody

data class WordList (
    val title: String,
    val visible: String,
    val addWordBy: String,
    val password: String?
)