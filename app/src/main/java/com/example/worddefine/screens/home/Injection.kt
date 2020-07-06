package com.example.worddefine.screens.home

import android.content.Context
import com.example.worddefine.data.FavoriteWordListRepository
import com.example.worddefine.data.WordListRepository
import com.example.worddefine.database.WordDefineDatabase
import com.example.worddefine.database.cache.FavoriteWordListLocalCache
import com.example.worddefine.database.cache.WordListLocalCache
import com.example.worddefine.database.cache.WordListTokenLocalCache
import com.example.worddefine.network.WordDefineApi
import java.util.concurrent.Executor

import java.util.concurrent.Executors

object Injection {

    private fun provideWordListCache(context: Context): WordListLocalCache {
        val database = WordDefineDatabase.getInstance(context)
        return WordListLocalCache(database!!.wordListDao(), Executors.newSingleThreadExecutor())
    }

    private fun provideWordListTokenCache(context: Context): WordListTokenLocalCache{
        val database = WordDefineDatabase.getInstance(context)
        return WordListTokenLocalCache(
            database!!.wordListTokenDao(),
            Executors.newSingleThreadExecutor())
    }

    private fun provideFavoriteWordListLocalCache(context: Context): FavoriteWordListLocalCache {
        val database = WordDefineDatabase.getInstance(context)
        return FavoriteWordListLocalCache(
            database!!.wordListDao(), Executors.newSingleThreadExecutor()
        )
    }

    fun provideWordListRepository(context: Context): WordListRepository {
        return WordListRepository(
            WordDefineApi.retrofitService,
            provideWordListCache(context),
            provideWordListTokenCache(context)
        )
    }

    private fun provideWordListTokenLocalCache(context: Context): WordListTokenLocalCache {
        val database = WordDefineDatabase.getInstance(context)
        return WordListTokenLocalCache(
            database!!.wordListTokenDao(),
            Executors.newSingleThreadExecutor()
        )
    }

    fun provideFavoriteWordListRepository(context: Context): FavoriteWordListRepository {
        return FavoriteWordListRepository(
            WordDefineApi.retrofitService,
            provideFavoriteWordListLocalCache(context),
            provideWordListTokenLocalCache(context)
        )
    }
}