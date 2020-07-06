package com.example.worddefine.screens.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.worddefine.databinding.WordListViewItemBinding
import com.example.worddefine.database.model.WordList

class WordListAdapter(private val onViewItemClickListener: OnViewItemClickListener,
                      private val onFavoriteClickListener: OnWidgetClickListener,
                      private val onMoreClickListener: OnWidgetClickListener):
    PagedListAdapter<WordList,
            WordListAdapter.WordListViewHolder>(DiffCallback) {

    class WordListViewHolder(private var binding: WordListViewItemBinding,
                             private val onFavoriteClickListener: OnWidgetClickListener,
                             private val onMoreClickListener: OnWidgetClickListener):
            RecyclerView.ViewHolder(binding.root){
        // bind wordList Property to view holder
        fun bind(wordList: WordList){
            // set all property to binding object
            binding.property = wordList
            // set click listener for favoriteImageView

 /*           binding.favoriteImageView.setOnClickListener {
                onFavoriteClickListener.onClick(wordList, it)
            }*/
            binding.favoriteCheckBox.setOnClickListener{
                onFavoriteClickListener.onClick(wordList, it)
            }

            // set click listener for threePointImageView
            binding.threePointImageView.setOnClickListener {
                onMoreClickListener.onClick(wordList, it)
            }
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<WordList>() {

        override fun areItemsTheSame(oldItem: WordList, newItem: WordList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WordList, newItem: WordList): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordListViewHolder {
        return WordListViewHolder(
            WordListViewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onFavoriteClickListener,
            onMoreClickListener
        )
    }

    override fun onBindViewHolder(holder: WordListViewHolder, position: Int) {
        val wordListProperty = getItem(position)
        // set on click listener on viewHolder
        holder.itemView.setOnClickListener{
            onViewItemClickListener.onClick(wordListProperty!!)
        }
        holder.bind(wordListProperty!!)
    }

    // recycler view on click listener on item click
    class OnViewItemClickListener(val clickListener: (wordList: WordList) -> Unit) {
        fun onClick(wordList: WordList) = clickListener(wordList)
    }

    // recycler view on favorite listener
    class OnWidgetClickListener(
        val clickListener: (wordList: WordList, view: View?) -> Unit
    ){
        fun onClick(wordList: WordList, view: View?)
                                        = clickListener(wordList, view)
    }
}