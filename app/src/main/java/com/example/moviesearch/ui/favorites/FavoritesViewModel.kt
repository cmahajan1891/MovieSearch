package com.example.moviesearch.ui.favorites

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviesearch.data.model.MovieMetaInfo
import com.example.moviesearch.data.repository.MoviesRepository
import com.example.moviesearch.di.FragmentScope
import com.example.moviesearch.utils.common.Resource
import com.example.moviesearch.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@FragmentScope
class FavoritesViewModel @Inject constructor(
    schedulerProvider: SchedulerProvider,
    private val compositeDisposable: CompositeDisposable,
    moviesRepository: MoviesRepository
) : ViewModel() {

    val movies: MutableLiveData<Resource<List<MovieMetaInfo>>> = MutableLiveData()

    init {
        onCreate(moviesRepository, schedulerProvider)
    }

    @VisibleForTesting
    fun onCreate(
        moviesRepository: MoviesRepository,
        schedulerProvider: SchedulerProvider
    ) {
        if (movies.value == null) {
            compositeDisposable.add(
                moviesRepository
                    .getAllFavoriteMoviesFromDb()
                    .subscribeOn(schedulerProvider.io())
                    .subscribe({
                        movies.postValue(Resource.success(it))
                    }, {
                        movies.postValue(Resource.error())
                    })
            )
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun updateListItem(movieMetaInfo: MovieMetaInfo) {
        val newMovies = ArrayList<MovieMetaInfo>()
        val resource = movies.value
        resource?.let {
            val movies = it.data
            val filterMovies = movies?.filterNot { info -> movieMetaInfo.imdbId == info.imdbId }
            if (filterMovies != null) {
                newMovies.addAll(filterMovies)
            }
            this.movies.postValue(Resource.success(newMovies))
        }
    }

}
