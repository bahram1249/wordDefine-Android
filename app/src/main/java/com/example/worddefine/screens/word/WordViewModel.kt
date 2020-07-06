package com.example.worddefine.screens.word

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.worddefine.database.WordDefineDatabase
import com.example.worddefine.database.model.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class WordViewModel(app: Application,
                    private val wordId: String) : AndroidViewModel(app) {


    private val _context = getApplication<Application>()
    private val _database: WordDefineDatabase = WordDefineDatabase.getInstance(_context)!!

    private val _viewModelJob = Job()
    private val _coroutineScope =
        CoroutineScope(_viewModelJob + Dispatchers.Default)

    private val _word = MutableLiveData<Word?>()
    val word: LiveData<Word?>
        get() = _word



    init {
        _coroutineScope.launch {
            _word.postValue(_database.wordDao().getWordById(wordId))
        }

    }

    override fun onCleared() {
        _viewModelJob.cancel()
    }
}
