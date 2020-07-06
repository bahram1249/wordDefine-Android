package com.example.worddefine.database.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "wordLists")
data class WordList(
    @PrimaryKey @Json(name = "_id") val id: String,
    var title: String,
    var visible: String,
    @ColumnInfo(name = "add_word_by") var addWordBy: String,
    @ColumnInfo(name = "user_id") var userId: String,
    @ColumnInfo(name = "user_name") var username: String,
    @ColumnInfo(name = "date_create") var dateCreate: Long,
    @ColumnInfo(name= "favorite_word_list") var favoriteWordList: String?,
    @ColumnInfo(name = "favorite_word_list_date_submitted") var favoriteDateSubmitted: Long?
)