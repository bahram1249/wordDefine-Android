package com.example.worddefine.screens.wordListEdit

import android.content.Context
import com.example.worddefine.data.WordListEditRepository
import com.example.worddefine.database.WordDefineDatabase
import com.example.worddefine.database.cache.WordListLocalCache
import com.example.worddefine.network.WordDefineApi
import java.util.concurrent.Executors

object Injection {

    private fun provideWordListCache(context: Context): WordListLocalCache {
        val database = WordDefineDatabase.getInstance(context)
        return WordListLocalCache(database!!.wordListDao(), Executors.newSingleThreadExecutor())
    }

    fun provideWordListEditRepository(context: Context): WordListEditRepository {
        return WordListEditRepository(
            WordDefineApi.retrofitService,
            provideWordListCache(context)
        )
    }
}