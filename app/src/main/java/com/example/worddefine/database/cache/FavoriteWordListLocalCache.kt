package com.example.worddefine.database.cache

import com.example.worddefine.database.dao.WordListDao
import com.example.worddefine.database.model.WordList
import com.example.worddefine.network.responseproperty.FavoriteWordList


import timber.log.Timber
import java.util.concurrent.Executor

class FavoriteWordListLocalCache (
    private val wordListDao: WordListDao,
    private val ioExecutor: Executor
){
    fun removeByWordListId(wordListId: String, removeFinished: ()-> Unit){
        ioExecutor.execute{
            Timber.d("removing FavoriteWordList By WordList: $wordListId")
            wordListDao.removeFavoriteWordList(wordListId)
            Timber.d("remove FavoriteWordList By WordList: $wordListId Finished")
            removeFinished()
        }
    }

    fun removeWordList(wordList: WordList, removeFinished: ()-> Unit){
        ioExecutor.execute{
            wordListDao.remove(wordList.id)
            removeFinished()
        }
    }

    fun insertAllWordList(wordLists: List<WordList>, insertFinished: () -> Unit){
        ioExecutor.execute{
            wordListDao.insertAll(wordLists)
            insertFinished()
        }
    }

    fun addFavoriteWordList(wordList: WordList, favoriteWordList: FavoriteWordList,
                            insertFinished: () -> Unit) {
        ioExecutor.execute {
            wordListDao.addFavoriteWordList(wordList.id, favoriteWordList.id)
            insertFinished()
        }
    }

}