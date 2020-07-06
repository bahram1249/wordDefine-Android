package com.example.worddefine.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "Users")
data class User(
    @PrimaryKey @Json(name = "_id") val id: String,
    var name: String
)