package com.example.worddefine.screens.wordEdit

import android.content.Context
import com.example.worddefine.data.WordEditRepository
import com.example.worddefine.database.WordDefineDatabase
import com.example.worddefine.database.cache.WordLocalCache
import com.example.worddefine.network.WordDefineApi
import java.util.concurrent.Executors

object Injection {

    private fun provideWordLocalCache(context: Context): WordLocalCache{
        val database = WordDefineDatabase.getInstance(context)
        return WordLocalCache(database!!.wordDao(), Executors.newSingleThreadExecutor())
    }

    fun provideWordEditRepository(context: Context): WordEditRepository{
        return WordEditRepository(
            WordDefineApi.retrofitService,
            provideWordLocalCache(context)
        )
    }
}