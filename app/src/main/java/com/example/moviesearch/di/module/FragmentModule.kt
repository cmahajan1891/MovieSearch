package com.example.moviesearch.di.module

import androidx.fragment.app.Fragment
import com.example.moviesearch.data.repository.MoviesRepository
import com.example.moviesearch.ui.moviedetail.MovieDetailViewModel
import com.example.moviesearch.ui.search.SearchFragmentViewModel
import com.example.moviesearch.utils.common.MemoryDataSource
import com.example.moviesearch.utils.getViewModel
import com.example.moviesearch.utils.network.NetworkHelper
import com.example.moviesearch.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class FragmentModule(
    private val fragment: Fragment,
    private val imdbId: String? = null
) {
    @Provides
    fun provideSearchFragmentViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        moviesRepository: MoviesRepository
    ): SearchFragmentViewModel = getViewModel(
        fragment,
        SearchFragmentViewModel::class
    ) {
        SearchFragmentViewModel(
            schedulerProvider,
            compositeDisposable,
            networkHelper,
            moviesRepository
        )
    }

    @Provides
    fun provideMovieDetailViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        moviesRepository: MoviesRepository,
        memoryDataSource: MemoryDataSource
    ): MovieDetailViewModel = getViewModel(
        fragment,
        MovieDetailViewModel::class
    ) {
        MovieDetailViewModel(
            schedulerProvider,
            compositeDisposable,
            networkHelper,
            moviesRepository,
            memoryDataSource,
            imdbId!!
        )
    }

}
