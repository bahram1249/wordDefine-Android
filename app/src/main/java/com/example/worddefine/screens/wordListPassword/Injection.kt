package com.example.worddefine.screens.wordListPassword

import android.content.Context
import com.example.worddefine.data.WordListPasswordRepository
import com.example.worddefine.database.WordDefineDatabase
import com.example.worddefine.database.cache.WordListTokenLocalCache
import com.example.worddefine.network.WordDefineApi
import com.example.worddefine.network.WordDefineApiService
import java.util.concurrent.Executors

object Injection {

    private fun provideWordListTokenCache(context: Context): WordListTokenLocalCache {
        val database = WordDefineDatabase.getInstance(context)
        return WordListTokenLocalCache(
            database!!.wordListTokenDao(),
            Executors.newSingleThreadExecutor())
    }

    fun provideWordListPasswordRepository(context: Context): WordListPasswordRepository{
        return WordListPasswordRepository(
            WordDefineApi.retrofitService,
            provideWordListTokenCache(context)
        )
    }
}