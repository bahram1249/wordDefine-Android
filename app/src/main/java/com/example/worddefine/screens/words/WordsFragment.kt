package com.example.worddefine.screens.words


import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView

import com.example.worddefine.R
import com.example.worddefine.Token
import com.example.worddefine.database.model.Word
import com.example.worddefine.databinding.FragmentWordsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class WordsFragment : Fragment() {

    private lateinit var viewModel: WordsViewModel
    private lateinit var binding: FragmentWordsBinding
    private lateinit var wordAccessToken: String
    private lateinit var wordListId: String
    private lateinit var userId: String
    private lateinit var wordListOwner: String
    private var accessToAddWord: Boolean = false
    private var visiblePassword: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initializeArguments()
        userId = Token.getUserId(context!!).toString()

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_words,
            container,
            false
        )


        initAdapter()

        binding.addWordFloatingActionButton.setOnClickListener {
            findNavController()
                .navigate(WordsFragmentDirections.actionWordsFragmentToWordEditFragment(
                    wordListId,
                    null,
                    visiblePassword))
        }

        setupScrollListener()

        val navController = this.findNavController()
        NavigationUI.setupWithNavController(binding.toolbar, navController)

        return binding.root
    }

    private fun initAdapter() {

        val application = requireNotNull(activity).application


        binding.lifecycleOwner = this

        val adapter = WordAdapter(
            onWordItemClickListener(),
            onMoreClickListener()
        )

        binding.words.adapter = adapter


        val viewModelFactory =
            WordsViewModelFactory(application,
                Injection.provideWordRepository(context!!),
                wordListId,
                wordAccessToken)
        viewModel = ViewModelProvider(this, viewModelFactory).get(WordsViewModel::class.java)

        viewModel.status.observe(this, Observer {
            if (it == false){

                binding.words.visibility = View.GONE
                binding.notFoundImageView.visibility = View.VISIBLE
                binding.notFoundAnythingTextView.visibility = View.VISIBLE

            }else{

                binding.words.visibility = View.VISIBLE
                binding.notFoundImageView.visibility = View.GONE
                binding.notFoundAnythingTextView.visibility = View.GONE

            }
        })

        viewModel.wordListOwner.observe(this, Observer {
            it?.let {
                wordListOwner = it
            }
        })

        viewModel.navigateToWord.observe(this, Observer {
            it?.let {
                findNavController()
                    .navigate(WordsFragmentDirections
                                .actionWordsFragmentToWordFragment(it.id, it.name))

                viewModel.navigateToWordDone()
            }
        })

        viewModel.navigateToWordEdit.observe(this, Observer {
            it?.let {
                findNavController()
                    .navigate(
                        WordsFragmentDirections.actionWordsFragmentToWordEditFragment(
                            wordListId, it.id, visiblePassword))
                viewModel.navigateToWordEditDone()
            }
        })

        viewModel.accessToAddWord.observe(this, Observer {
            it?.let {
                accessToAddWord = it
                if (it)
                    binding.addWordFloatingActionButton.visibility = View.VISIBLE
                //viewModel.accessToAddWordAssigned()
            }
        })

        viewModel.visiblePassword.observe(this, Observer {
            it?.let {
                visiblePassword = it
                //viewModel.visiblePasswordAssigned()
            }
        })

        binding.viewModel = viewModel

    }

    override fun onResume() {
        viewModel.statusChanged()
        super.onResume()
    }

    private fun initializeArguments() {
        wordAccessToken = WordsFragmentArgs.fromBundle(this.arguments!!).wordAccessToken
        wordListId = WordsFragmentArgs.fromBundle(this.arguments!!).wordListId
    }

    private fun onShareClickListener(word: Word) {
        startActivity(viewModel.shareIntent(word))
    }

    private fun onMoreClickListener(): WordAdapter.OnWidgetClickListener{
        return WordAdapter.OnWidgetClickListener { it, view ->
            val popupMenu: PopupMenu = PopupMenu(context, view, Gravity.CENTER)
            if (it.userId == userId || it.userId == wordListOwner)
                popupMenu.menuInflater.inflate(R.menu.popup_word_menu, popupMenu.menu)
            else
                popupMenu.menuInflater.inflate(R.menu.popup_word_others_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener (PopupMenu.OnMenuItemClickListener{item ->
                when(item.itemId){
                    R.id.delete_action-> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(getString(R.string.are_you_sure_want_to_delete_this_word))
                            .setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener() {
                                    dialogInterface: DialogInterface, i: Int ->
                                viewModel.onWordDelete(it)
                            })
                            .setNegativeButton(getString(R.string.no), null)
                            .show()
                    }

                    R.id.edit_action-> {
                        viewModel.onWordEditClick(it)
                    }

                    R.id.share_action -> { onShareClickListener(it)

                    }
                }
                true
            })

            popupMenu.show()
        }
    }

    private fun onWordItemClickListener(): WordAdapter.OnViewItemClickListener{
        return WordAdapter.OnViewItemClickListener{
            it -> viewModel.onWordItemClick(it)
        }
    }

    private fun setupScrollListener() {
        val layoutManager =
            binding.words.layoutManager as androidx.recyclerview.widget.LinearLayoutManager

        binding.words.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                super.onScrolled(recyclerView, dx, dy)


                if (dy > 0 || dy < 0 && binding.addWordFloatingActionButton.isShown)
                    binding.addWordFloatingActionButton.hide()

                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                viewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    binding.addWordFloatingActionButton.show()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}
