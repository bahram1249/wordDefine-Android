package com.example.worddefine.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.worddefine.database.model.Word

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE wordList = :wordListId ORDER BY date_create DESC")
    fun wordsByWordListId(wordListId: String): DataSource.Factory<Int, Word>

    @Query("SELECT count(*) FROM words WHERE wordList = :wordListId")
    fun wordsByWordListIdCount(wordListId: String): Int

    @Query("SELECT * FROM words WHERE id = :wordId")
    fun getWordById(wordId: String): Word?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(words: List<Word>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: Word)

    @Query("DELETE FROM words WHERE id = :id")
    fun remove(id: String)
}