package com.example.worddefine.screens.favorites

import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.example.worddefine.database.model.WordList
import com.example.worddefine.screens.home.WordListAdapter

@BindingAdapter("favoriteWordListsData")
fun bindRecyclerView(recyclerView: RecyclerView, data: PagedList<WordList>?) {
    val adapter = recyclerView.adapter as WordListAdapter
    adapter.submitList(data)
//    adapter.notifyDataSetChanged()
}