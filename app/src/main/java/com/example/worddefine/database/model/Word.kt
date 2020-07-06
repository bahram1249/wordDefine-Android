package com.example.worddefine.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "words")
data class Word(
    @PrimaryKey @Json(name = "_id") val id: String,
    var name: String,
    var definition: String,
    var examples: String,
    var lang: String,
    @ColumnInfo(name = "date_create") var dateCreate: Long,
    @ColumnInfo(name = "user_id") var userId: String,
    @ColumnInfo(name = "user_name") var username: String,
    var wordList: String
)