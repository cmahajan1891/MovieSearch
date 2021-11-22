package com.example.moviesearch.ui.moviedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.moviesearch.BR
import com.example.moviesearch.MovieSearchApplication
import com.example.moviesearch.R
import com.example.moviesearch.databinding.MovieDetailFragmentBinding
import com.example.moviesearch.di.component.DaggerFragmentComponent
import com.example.moviesearch.di.module.FragmentModule
import com.example.moviesearch.utils.common.Status
import com.example.moviesearch.utils.common.toVisibleOrGone
import com.example.moviesearch.utils.display.Toaster
import javax.inject.Inject

class MovieDetailFragment : Fragment() {

    @Inject
    lateinit var viewModel: MovieDetailViewModel
    private lateinit var movieDetailFragmentBinding: MovieDetailFragmentBinding
    private val args: MovieDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        movieDetailFragmentBinding = MovieDetailFragmentBinding.inflate(inflater, container, false)
        return movieDetailFragmentBinding.root
    }

    private fun injectDependencies() {
        DaggerFragmentComponent
            .builder()
            .applicationComponent((requireActivity().application as MovieSearchApplication).applicationComponent)
            .fragmentModule(FragmentModule(this, imdbId = args.imdbId))
            .build()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupViews()
    }

    private fun setupViews() {
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val appBarConfiguration = AppBarConfiguration(navHostFragment.navController.graph)
        movieDetailFragmentBinding.collapsingToolbarLayout.setupWithNavController(
            movieDetailFragmentBinding.toolbar,
            navHostFragment.navController,
            appBarConfiguration
        )
    }

    private fun setupObservers() {
        viewModel.progressBarShown.observe(viewLifecycleOwner, {
            movieDetailFragmentBinding.spinner.visibility = it.toVisibleOrGone()
            movieDetailFragmentBinding.sectionDivider.visibility = (!it).toVisibleOrGone()
        })
        viewModel.messageStringId.observe(viewLifecycleOwner, {
            if (it.status == Status.ERROR) {
                it.data?.run { showMessage(this) }
            }
        })
        viewModel.movieDetails.observe(viewLifecycleOwner, {
            it?.let {
                movieDetailFragmentBinding.collapsingToolbarLayout.title = it.title
                movieDetailFragmentBinding.movieDirector.text =
                    (requireContext().getString(R.string.director) + ": " + it.director)
                movieDetailFragmentBinding.moviePlot.text =
                    if (it.plot.startsWith("N/A")) "" else it.plot
                movieDetailFragmentBinding.setVariable(BR.movieItemDetail, it)
                movieDetailFragmentBinding.executePendingBindings()
            }
        })
    }

    private fun showMessage(@StringRes res: Int) {
        context?.let { Toaster.show(it, getString(res)) }
    }

}
