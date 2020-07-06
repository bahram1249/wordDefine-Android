package com.example.worddefine.screens.words

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.example.worddefine.R
import com.example.worddefine.database.model.Word

@BindingAdapter("wordsData")
fun bindRecyclerView(recyclerView: RecyclerView, data: PagedList<Word>?) {
    val adapter = recyclerView.adapter as WordAdapter
    adapter.submitList(data)
//    adapter.notifyDataSetChanged()
}

@BindingAdapter("wordName")
fun bindWordName(textView: TextView, wordName: String){
    if(wordName.length > 15)
        textView.text = wordName.substring(0, 15).plus(" ..." )
    else
        textView.text = wordName
}

@BindingAdapter("wordDefinition")
fun bindWordDefinition(textView: TextView, wordDefinition: String){
    if(wordDefinition.length > 15)
        textView.text = wordDefinition.substring(0, 15).plus(" ..." )
    else
        textView.text = wordDefinition
}