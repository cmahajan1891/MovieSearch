package com.example.moviesearch.ui.moviedetail

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviesearch.R
import com.example.moviesearch.data.model.MovieDetails
import com.example.moviesearch.data.repository.MoviesRepository
import com.example.moviesearch.utils.common.MemoryDataSource
import com.example.moviesearch.utils.common.Resource
import com.example.moviesearch.utils.network.NetworkHelper
import com.example.moviesearch.utils.network.NetworkHelper.Companion.RESPONSE_INVALID
import com.example.moviesearch.utils.rx.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class MovieDetailViewModel(
    private val schedulerProvider: SchedulerProvider,
    private val compositeDisposable: CompositeDisposable,
    private val networkHelper: NetworkHelper,
    private val moviesRepository: MoviesRepository,
    private val memoryDataSource: MemoryDataSource,
    private val imdbId: String
) : ViewModel() {

    val movieDetails: MutableLiveData<MovieDetails> = MutableLiveData()
    val messageStringId: MutableLiveData<Resource<Int>> = MutableLiveData()
    val progressBarShown: MutableLiveData<Boolean> = MutableLiveData()

    init {
        onCreate()
    }

    @VisibleForTesting
    fun onCreate() {
        if (movieDetails.value == null && checkInternetConnectionWithMessage()) {
            progressBarShown.postValue(true)
            val memoryData =
                memoryDataSource.getItem(imdbId).subscribeOn(schedulerProvider.computation())
            val apiData = moviesRepository.getMovieDetails(imdbId).map {
                Observable.create<MovieDetails> { emitter ->
                    memoryDataSource.addItem(imdbId, movieDetails = it)
                    emitter.onComplete()
                }.subscribeOn(schedulerProvider.computation()).subscribe()
                it
            }.subscribeOn(schedulerProvider.io())
            compositeDisposable.add(
                Observable.concat(memoryData, apiData)
                    .firstElement()
                    .subscribe({
                        // Log.e("MovieDetailViewModel", it.toString())
                        progressBarShown.postValue(false)
                        when (it.response) {
                            RESPONSE_INVALID -> {
                                handleNetworkError(it.error)
                            }
                            else -> {
                                movieDetails.postValue(it)
                            }
                        }
                    }, {
                        progressBarShown.postValue(false)
                        handleNetworkError(it.message.toString())
                    })
            )
        }
    }

    private fun checkInternetConnectionWithMessage(): Boolean {
        return if (networkHelper.isNetworkConnected()) {
            true
        } else {
            messageStringId.postValue(Resource.error(R.string.network_connection_error))
            false
        }
    }

    private fun handleNetworkError(error: String) {
        return error.run {
            when (this) {
                NetworkHelper.GETTING_DATA_ERROR -> messageStringId.postValue(Resource.error(R.string.problem_getting_details))
                else -> messageStringId.postValue(Resource.error(R.string.something_went_wrong))
            }
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}
