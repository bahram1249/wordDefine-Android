package com.example.worddefine.database.cache

import com.example.worddefine.database.dao.WordListDao
import com.example.worddefine.network.responseproperty.FavoriteWordList
import com.example.worddefine.database.model.WordList
import timber.log.Timber
import java.util.concurrent.Executor

class WordListLocalCache(
    private val wordListDao: WordListDao,
    private val ioExecutor: Executor
) {
    fun insertAll(wordLists: List<WordList>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            Timber.d("inserting ${wordLists.size} wordLists")
            wordListDao.insertAll(wordLists)
            Timber.d("inserting ${wordLists.size} wordLists Finished")
            insertFinished()
        }
    }

    fun insert(wordList: WordList, insertFinished: () -> Unit){
        ioExecutor.execute{
            Timber.d("Inserting wordList: ${wordList.id}")
            wordListDao.insert(wordList)
            Timber.d("Inserting wordList: ${wordList.id} is Finished")
            insertFinished()
        }
    }

    fun remove(wordList: WordList, removeFinished: () -> Unit) {
        ioExecutor.execute {
            Timber.d("removing ${wordList.title} from wordLists")
            wordListDao.remove(wordList.id)
            Timber.d("removing ${wordList.title} from wordLists Finished")
            removeFinished()
        }

    }

    fun removeFavoriteWordList(wordList: WordList, removeFinished: () -> Unit) {
        ioExecutor.execute{
            wordListDao.removeFavoriteWordList(wordList.id)
            removeFinished()
        }

    }

    fun addFavoriteWordList(wordList: WordList, favoriteWordList: FavoriteWordList,
                            insertFinished: () -> Unit) {
        ioExecutor.execute {
            wordListDao.addFavoriteWordList(wordList.id, favoriteWordList.id)
            insertFinished()
        }
    }

    fun getWordListById(wordListId: String, queryFinished: (wordList: WordList?) -> Unit){
        ioExecutor.execute{
            val wordList = wordListDao.getWordListById(wordListId)
            queryFinished(wordList)
        }
    }
}
