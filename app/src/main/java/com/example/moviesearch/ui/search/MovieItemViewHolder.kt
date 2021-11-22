package com.example.moviesearch.ui.search

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesearch.BR
import com.example.moviesearch.MovieSearchApplication
import com.example.moviesearch.data.model.MovieMetaInfo
import com.example.moviesearch.databinding.ItemMovieBinding
import com.example.moviesearch.di.component.DaggerViewHolderComponent
import com.example.moviesearch.di.module.ViewHolderModule
import com.example.moviesearch.ui.favorites.FavoritesFragment
import com.example.moviesearch.ui.favorites.FavoritesFragmentDirections
import javax.inject.Inject

class MovieItemViewHolder(
    private val movieItemBinding: ItemMovieBinding,
    private val fragmentTag: String,
    private val callback: (movieMetaInfo: MovieMetaInfo) -> Unit
) :
    RecyclerView.ViewHolder(movieItemBinding.root), LifecycleOwner {

    init {
        onCreate()
    }

    @Inject
    lateinit var viewModel: MovieItemViewModel

    @Inject
    lateinit var lifecycleRegistry: LifecycleRegistry

    override fun getLifecycle() = lifecycleRegistry

    private fun onCreate() {
        injectDependencies()
        lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
        setupObservers()
        setupViews()
    }

    private fun injectDependencies() {
        DaggerViewHolderComponent
            .builder()
            .applicationComponent((itemView.context.applicationContext as MovieSearchApplication).applicationComponent)
            .viewHolderModule(ViewHolderModule(this))
            .build()
            .inject(this)
    }

    private fun setupObservers() {
        viewModel.data.observe(this, {
            movieItemBinding.setVariable(BR.movieItem, it)
            movieItemBinding.executePendingBindings()
        })
        viewModel.isFavStatusChanged.observe(this, {
            if (it) {
                callback.invoke(viewModel.data.value!!)
                viewModel.itemUpdated()
            }
        })
    }

    private fun setupViews() {
        movieItemBinding.setClickListener {
            navigateToMovieDetail(viewModel.data.value!!.imdbId)
        }
        movieItemBinding.favorite.setOnClickListener {
            addToFavorites(viewModel.data.value!!)
        }
    }

    fun onStart() {
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
    }

    fun onStop() {
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

    fun onDestroy() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
    }

    fun bind(data: MovieMetaInfo) {
        viewModel.updateData(data)
    }

    private fun addToFavorites(movieItem: MovieMetaInfo) {
        viewModel.onFavoriteClick(movieItem)
    }

    private fun navigateToMovieDetail(imdbId: String) {
        when (fragmentTag) {
            SearchFragment.TAG -> {
                Navigation.findNavController(itemView).navigate(
                    SearchFragmentDirections.actionMainPageToMovieDetailFragment(
                        imdbId
                    )
                )
            }
            FavoritesFragment.TAG -> {
                Navigation.findNavController(itemView).navigate(
                    FavoritesFragmentDirections.actionFavoritesPageToMovieDetail(
                        imdbId
                    )
                )
            }
        }
    }

}
