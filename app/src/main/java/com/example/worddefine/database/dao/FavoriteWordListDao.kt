package com.example.worddefine.database.dao

import androidx.paging.DataSource
import androidx.room.*
import com.example.worddefine.database.model.FavoriteWordList


@Dao
interface FavoriteWordListDao{
    @Query("SELECT * FROM favoriteWordLists ORDER BY id")
    fun getFavoriteWordLists(): DataSource.Factory<Int, FavoriteWordList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favoriteWordList: FavoriteWordList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(favoriteWordLists: List<FavoriteWordList>)

    @Query("DELETE FROM favoriteWordLists WHERE word_list = :wordListId")
    fun removeByWordListId(wordListId: String)
}