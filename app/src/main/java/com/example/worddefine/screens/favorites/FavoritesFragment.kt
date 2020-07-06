package com.example.worddefine.screens.favorites

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView

import com.example.worddefine.R
import com.example.worddefine.Token
import com.example.worddefine.database.model.WordList
import com.example.worddefine.databinding.FragmentFavoritesBinding
import com.example.worddefine.screens.home.Injection
import com.example.worddefine.screens.home.WordListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment() {


    private lateinit var viewModel: FavoritesViewModel

    private lateinit var binding: FragmentFavoritesBinding

    private lateinit var adapter: WordListAdapter

    private lateinit var userId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(activity).application

        userId = Token.getUserId(context!!).toString()

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_favorites,
            container,
            false
        )

        // allow to observe view model
        binding.lifecycleOwner = this

        val viewModelFactory =
            FavoritesViewModelFactory(
                application,
                Injection.provideFavoriteWordListRepository(context!!)
            )

        // get instance of viewModel
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(FavoritesViewModel::class.java)

        // set view Model in data binding
        binding.viewModel = viewModel

        initAdapter()
        setupScrollListener()

        return binding.root
    }

    private fun initAdapter() {

        // on recyclerView item click
        val onItemClickListener = onItemClickListener()
        // on favorite widget in recyclerView click
        val onFavoriteClickListener = onFavoriteClickListener()
        // on threePoint widget in recyclerView click
        val onMoreClickListener = onMoreClickListener()

        adapter = WordListAdapter(
            onItemClickListener,
            onFavoriteClickListener,
            onMoreClickListener
        )

        binding.wordLists.adapter = adapter


        viewModel.isRequestInProgress.observe(this, Observer {
            if(it){
                binding.progressBar.visibility = View.VISIBLE
            }
            else {
                binding.progressBar.visibility = View.GONE
            }
        })

        // observe result message for show the toast
        viewModel.resultMessage.observe(this,  Observer {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.resultMessageDone()
            }
        })

        viewModel.snackMessage.observe(this, Observer {
            it?.let {
                Snackbar.make(binding.progressBar, it, Snackbar.LENGTH_SHORT).show()
                viewModel.snackMessageDone()
            }
        })

        viewModel.navigateToWords.observe(this, Observer {
            it?.let {
                findNavController().navigate(
                    FavoritesFragmentDirections
                        .actionFavoritesFragmentToWordsFragment(
                            it.token, it.wordListId, it.wordListTitle))

                viewModel.onNavigateToWordsDone()
            }
        })

        viewModel.navigateToWordListPassword.observe(this, Observer {
            it?.let {
                findNavController().navigate(
                    FavoritesFragmentDirections.actionFavoritesFragmentToWordListPasswordFragment(
                        it.id, it.title
                    )
                )

                viewModel.onNavigateToWordListPasswordDone()
            }
        })

    }

    private fun onItemClickListener(): WordListAdapter.OnViewItemClickListener {
        return WordListAdapter.OnViewItemClickListener {
            viewModel.onWordListClick(it)
        }
    }

    private fun onFavoriteClickListener(): WordListAdapter.OnWidgetClickListener {
        return WordListAdapter.OnWidgetClickListener { it, view ->
            // request to delete or add this wordList to favorite
            viewModel.onFavoriteClick(it)
        }
    }

    private fun onShareClickListener(wordList: WordList) {
        startActivity(viewModel.shareIntent(wordList))
    }

    private fun onEditClickListener(wordList: WordList) {
        findNavController()
            .navigate(FavoritesFragmentDirections
                        .actionFavoritesFragmentToWordListEditFragment(wordList.id))
    }

    private fun onMoreClickListener(): WordListAdapter.OnWidgetClickListener {
        return WordListAdapter.OnWidgetClickListener { it, view ->

            val popupMenu: PopupMenu = PopupMenu(context, view, Gravity.CENTER)

            if (it.userId == userId)
                popupMenu.menuInflater.inflate(R.menu.popup_word_list_menu, popupMenu.menu)
            else
                popupMenu.menuInflater.inflate(R.menu.popup_word_list_others_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->

                when (item.itemId) {
                    R.id.delete_action -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(getString(R.string.are_you_sure_to_delete_this_word_list))
                            .setPositiveButton(
                                getString(R.string.yes),
                                DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                                    viewModel.onDeleteWordListProperty(it)
                                })
                            .setNegativeButton(getString(R.string.no), null)
                            .show()
                    }
                    R.id.share_action -> onShareClickListener(it)

                    R.id.edit_action -> onEditClickListener(it)
                }

                true
            })

            popupMenu.show()
        }
    }

    private fun setupScrollListener() {
        val layoutManager =
            binding.wordLists.layoutManager as androidx.recyclerview.widget.LinearLayoutManager

        binding.wordLists.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                viewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount)
            }

        })
    }


}
