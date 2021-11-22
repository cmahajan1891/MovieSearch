package com.example.moviesearch.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviesearch.MovieSearchApplication
import com.example.moviesearch.databinding.FavoritesFragmentBinding
import com.example.moviesearch.di.component.DaggerFragmentComponent
import com.example.moviesearch.di.module.FragmentModule
import com.example.moviesearch.ui.search.MoviesAdapter
import javax.inject.Inject

class FavoritesFragment : Fragment() {

    companion object {
        const val TAG = "FavoritesFragment"
    }

    @Inject
    lateinit var viewModel: FavoritesViewModel
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var favoritesFragmentBinding: FavoritesFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favoritesFragmentBinding =
            FavoritesFragmentBinding.inflate(LayoutInflater.from(context), container, false)
        moviesAdapter = MoviesAdapter(this.lifecycle, TAG) { movieMetaInfo ->
            viewModel.updateListItem(movieMetaInfo)
        }
        favoritesFragmentBinding.favoriteMovies.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = moviesAdapter
        }
        return favoritesFragmentBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun injectDependencies() {
        DaggerFragmentComponent
            .builder()
            .applicationComponent((requireActivity().application as MovieSearchApplication).applicationComponent)
            .fragmentModule(FragmentModule(this))
            .build()
            .inject(this)
    }

    private fun setupObservers() {
        viewModel.movies.observe(viewLifecycleOwner, {
            it?.data?.run { moviesAdapter.submitList(it.data) }
        })
    }

}