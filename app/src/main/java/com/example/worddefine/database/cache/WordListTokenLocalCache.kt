package com.example.worddefine.database.cache

import com.example.worddefine.database.dao.WordListTokenDao
import com.example.worddefine.database.model.WordList
import com.example.worddefine.database.model.WordListToken
import timber.log.Timber
import java.util.concurrent.Executor


class WordListTokenLocalCache (
    private val wordListTokenDao: WordListTokenDao,
    private val ioExecutor: Executor
){
    fun insert(wordListId: String, wordListTitle: String, token: String, insertFinished: () -> Unit ){
        ioExecutor.execute{
            val wordListToken = WordListToken(wordListId, token, wordListTitle)
            Timber.d("inserting word list token for ${wordListToken.wordListId}")
            wordListTokenDao.insert(wordListToken)
            Timber.d("""token: ${wordListToken.token} 
                |inserted for wordList: ${wordListToken.wordListId}""".trimMargin())
            insertFinished();
        }
    }

    fun getWordListToken(wordList: WordList, insertFinished: (wordListToken: WordListToken?) -> Unit){
        ioExecutor.execute{
            val wordListToken = wordListTokenDao.getTokenByWordListId(wordList.id)
            insertFinished(wordListToken)
        }
    }
}