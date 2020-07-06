package com.example.worddefine.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.worddefine.database.dao.FavoriteWordListDao
import com.example.worddefine.database.dao.WordDao
import com.example.worddefine.database.dao.WordListDao
import com.example.worddefine.database.dao.WordListTokenDao
import com.example.worddefine.database.model.*

@Database(entities =
            [   WordList::class,
                FavoriteWordList::class,
                User::class, Word::class,
                WordListToken::class]
        , version = 4)
abstract class WordDefineDatabase : RoomDatabase() {

    abstract fun wordListDao(): WordListDao
    abstract fun favoriteWordListDao(): FavoriteWordListDao
    abstract fun wordDao(): WordDao
    abstract fun wordListTokenDao(): WordListTokenDao
    //abstract fun userDao(): UserDao

    companion object {
        //@Volatile
        private var instance: WordDefineDatabase? = null

        fun getInstance(context: Context): WordDefineDatabase? {

            val tempInstance = instance
            if(tempInstance != null){
                return tempInstance
            }

            synchronized(this) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordDefineDatabase::class.java,
                    "word_define_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return instance
        }
    }

}