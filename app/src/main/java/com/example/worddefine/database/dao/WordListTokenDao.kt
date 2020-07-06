package com.example.worddefine.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.worddefine.database.model.WordListToken

@Dao
interface WordListTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wordListToken: WordListToken)

    @Query("SELECT * FROM wordLists_tokens WHERE wordListId = :wordListId")
    fun getTokenByWordListId(wordListId: String): WordListToken?
}