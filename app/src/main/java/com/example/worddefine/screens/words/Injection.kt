package com.example.worddefine.screens.words

import android.content.Context
import com.example.worddefine.data.WordRepository
import com.example.worddefine.database.WordDefineDatabase
import com.example.worddefine.database.cache.WordLocalCache
import com.example.worddefine.network.WordDefineApi
import java.util.concurrent.Executors

object Injection {

    private fun provideWordCache(context: Context): WordLocalCache {
        val database = WordDefineDatabase.getInstance(context)
        return WordLocalCache(database!!.wordDao(), Executors.newSingleThreadExecutor())
    }

    fun provideWordRepository(context: Context): WordRepository {
        return WordRepository(WordDefineApi.retrofitService, provideWordCache(context))
        /*provideFavoriteWordListLocalCache(context)*/
    }
}