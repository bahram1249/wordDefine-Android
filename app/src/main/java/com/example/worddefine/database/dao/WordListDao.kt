package com.example.worddefine.database.dao

import androidx.paging.DataSource
import androidx.room.*
import com.example.worddefine.database.model.WordList

@Dao
interface WordListDao{
    @Query("SELECT * FROM wordLists ORDER BY date_create DESC")
    fun wordListsEveryOne(): DataSource.Factory<Int, WordList>

    @Query("SELECT * FROM wordLists WHERE user_id = :userId ORDER BY date_create DESC")
    fun wordListsOnlyMe(userId: String): DataSource.Factory<Int, WordList>

    @Query("""SELECT * FROM wordLists 
                    WHERE visible = 'userWithPassword' 
                    ORDER BY date_create DESC""")
    fun wordListsUserWithPassword(): DataSource.Factory<Int, WordList>

    @Query("""SELECT * FROM wordLists
                    WHERE favorite_word_list IS NOT NULL
                    ORDER BY favorite_word_list_date_submitted DESC""")
    fun wordListsIsFavorite(): DataSource.Factory<Int, WordList>

    @Query("""SELECT * FROM wordLists
                    WHERE id= :wordListId
                    LIMIT 1""")
    fun getWordListById(wordListId: String): WordList?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wordList: WordList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(wordLists: List<WordList>)

    @Query("DELETE FROM wordLists WHERE id = :id")
    fun remove(id: String)

    @Query("UPDATE wordLists SET favorite_word_list = null WHERE id = :wordListId ")
    fun removeFavoriteWordList(wordListId: String)

    @Query("""UPDATE wordLists
                    SET favorite_word_list = :favoriteWordListId
                    WHERE id = :wordListId""")
    fun addFavoriteWordList(wordListId: String, favoriteWordListId: String)
}