package com.example.worddefine.database.cache

import com.example.worddefine.database.dao.WordDao
import com.example.worddefine.database.dao.WordListDao
import com.example.worddefine.database.model.Word
import com.example.worddefine.network.responseproperty.FavoriteWordList
import com.example.worddefine.database.model.WordList
import timber.log.Timber
import java.util.concurrent.Executor

class WordLocalCache(
    private val wordDao: WordDao,
    private val ioExecutor: Executor
) {
    fun insertAll(words: List<Word>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            Timber.d("inserting ${words.size} words")
            wordDao.insertAll(words)
            Timber.d("inserting ${words.size} words Finished")
            insertFinished()
        }
    }

    fun insert(word: Word, insertFinished: () -> Unit){
        ioExecutor.execute{
            Timber.d("inserting ${word.id} word")
            wordDao.insert(word)
            Timber.d("inserting ${word.id} Finished")
            insertFinished()
        }
    }

    fun remove(word: Word, removeFinished: () -> Unit) {
        ioExecutor.execute {
            Timber.d("removing ${word.name} from words")
            wordDao.remove(word.id)
            Timber.d("removing ${word.name} from words Finished")
            removeFinished()
        }
    }

    fun removeById(wordId: String, removeFinished: () -> Unit){
        ioExecutor.execute {
            Timber.d("removing ${wordId} from words")
            wordDao.remove(wordId)
            Timber.d("removing ${wordId} from words")
            removeFinished()
        }
    }

    fun wordsByWordListIdCount(wordListId: String, queryFinished: (count: Int) -> Unit) {
        ioExecutor.execute{
            val wordCount = wordDao.wordsByWordListIdCount(wordListId)
            queryFinished(wordCount)
        }
    }

    fun getWordById(wordId: String, queryFinished: (word: Word?) -> Unit) {
        ioExecutor.execute{
            val word = wordDao.getWordById(wordId)
            queryFinished(word)
        }
    }
}
