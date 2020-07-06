package com.example.worddefine.screens.words

import com.example.worddefine.database.model.Word
import com.example.worddefine.databinding.WordViewItemBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


class WordAdapter(private val onViewItemClickListener: OnViewItemClickListener,
                      private val onMoreClickListener: OnWidgetClickListener):
    PagedListAdapter<Word,
            WordAdapter.WordViewHolder>(DiffCallback) {

    class WordViewHolder(private var binding: WordViewItemBinding,
                             private val onMoreClickListener: OnWidgetClickListener):
        RecyclerView.ViewHolder(binding.root){
        // bind word Property to view holder
        fun bind(word: Word){
            // set all property to binding object
            binding.property = word

            // set click listener for threePointImageView
            binding.threePointImageView.setOnClickListener {
                onMoreClickListener.onClick(word, it)
            }
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Word>() {

        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder(
            WordViewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onMoreClickListener
        )
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val wordProperty = getItem(position)
        // set on click listener on viewHolder
        holder.itemView.setOnClickListener{
            onViewItemClickListener.onClick(wordProperty!!)
        }
        holder.bind(wordProperty!!)
    }

    // recycler view on click listener on item click
    class OnViewItemClickListener(val clickListener: (word: Word) -> Unit) {
        fun onClick(word: Word) = clickListener(word)
    }

    // recycler view on favorite listener
    class OnWidgetClickListener(
        val clickListener: (word: Word, view: View?) -> Unit
    ){
        fun onClick(word: Word, view: View?)
                = clickListener(word, view)
    }
}