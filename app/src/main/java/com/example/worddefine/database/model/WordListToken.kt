package com.example.worddefine.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "wordLists_tokens")
data class WordListToken (
    @PrimaryKey
    val wordListId: String,
    val token: String,
    val wordListTitle: String
)