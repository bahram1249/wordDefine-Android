package com.example.worddefine.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "favoriteWordLists")
data class FavoriteWordList(
    @PrimaryKey @Json(name = "_id") var id: String,
    @ColumnInfo(name = "word_list") var wordList: String,
    @ColumnInfo(name = "date_submitted") var dateSubmitted: Long?
)