package com.example.moviesearch.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesearch.MovieSearchApplication
import com.example.moviesearch.R
import com.example.moviesearch.databinding.SearchFragmentBinding
import com.example.moviesearch.di.component.DaggerFragmentComponent
import com.example.moviesearch.di.module.FragmentModule
import com.example.moviesearch.utils.common.Status
import com.example.moviesearch.utils.display.Toaster
import javax.inject.Inject

class SearchFragment : Fragment() {

    companion object {
        const val TAG = "SearchFragment"
    }

    @Inject
    lateinit var searchFragmentViewModel: SearchFragmentViewModel
    private lateinit var searchFragmentBinding: SearchFragmentBinding
    private lateinit var moviesAdapter: MoviesAdapter
    private var queryTextListener: SearchView.OnQueryTextListener? = null
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchFragmentBinding =
            SearchFragmentBinding.inflate(LayoutInflater.from(context), container, false)
        moviesAdapter = MoviesAdapter(this.lifecycle, TAG) { movieMetaInfo ->
            searchFragmentViewModel.updateListItem(movieMetaInfo)
        }
        searchFragmentBinding.moviesView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = moviesAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    layoutManager?.run {
                        if (this is LinearLayoutManager
                            && itemCount > 0
                            && itemCount == findLastVisibleItemPosition() + 1
                        ) searchFragmentViewModel.onLoadMore()
                    }
                }
            })
        }
        return searchFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        searchFragmentViewModel.setupSearchSubscription()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        setupSearchView(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> return false
            else -> {
            }
        }
        searchView?.setOnQueryTextListener(queryTextListener)
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        searchView?.setOnQueryTextListener(null)
        super.onDestroyView()
    }

    private fun injectDependencies() {
        DaggerFragmentComponent
            .builder()
            .applicationComponent((requireActivity().application as MovieSearchApplication).applicationComponent)
            .fragmentModule(FragmentModule(this))
            .build()
            .inject(this)
    }

    private fun setupSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.action_search)
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = (searchItem?.actionView as? SearchView)
        searchView?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            queryTextListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    Log.i("onQueryTextChange", newText)
                    searchFragmentViewModel.updateQuery(newText, 1)
                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.i("onQueryTextSubmit", query)
                    clearFocus()
                    return true
                }
            }
            setOnQueryTextListener(queryTextListener)
            if (!searchFragmentViewModel.query.value.isNullOrEmpty() && this.query.toString() != searchFragmentViewModel.query.value) {
                setQuery(searchFragmentViewModel.query.value, false)
            }
        }
    }

    private fun setupObservers() {
        searchFragmentViewModel.messageStringId.observe(viewLifecycleOwner, {
            if (it.status == Status.EMPTY || it.status == Status.ERROR) {
                it.data?.run { showMessage(this) }
            }
        })
        searchFragmentViewModel.movies.observe(viewLifecycleOwner, {
            it?.data?.run { moviesAdapter.submitList(it.data) }
        })
    }

    private fun showMessage(@StringRes res: Int) {
        context?.let { Toaster.show(it, getString(res)) }
    }

}
