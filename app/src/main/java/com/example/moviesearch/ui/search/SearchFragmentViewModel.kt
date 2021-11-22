package com.example.moviesearch.ui.search

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviesearch.R
import com.example.moviesearch.data.model.MovieMetaInfo
import com.example.moviesearch.data.repository.MoviesRepository
import com.example.moviesearch.utils.common.Resource
import com.example.moviesearch.utils.network.NetworkHelper
import com.example.moviesearch.utils.network.NetworkHelper.Companion.MOVIE_NOT_FOUND_ERROR
import com.example.moviesearch.utils.network.NetworkHelper.Companion.RESPONSE_INVALID
import com.example.moviesearch.utils.network.NetworkHelper.Companion.TOO_MANY_RESULTS_ERROR
import com.example.moviesearch.utils.rx.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import java.util.concurrent.TimeUnit

class SearchFragmentViewModel(
    private val schedulerProvider: SchedulerProvider,
    private val compositeDisposable: CompositeDisposable,
    private val networkHelper: NetworkHelper,
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private var subject = PublishProcessor.create<Pair<String, Int>>()
    @VisibleForTesting
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val movies: MutableLiveData<Resource<List<MovieMetaInfo>>> = MutableLiveData()
    val messageStringId: MutableLiveData<Resource<Int>> = MutableLiveData()
    var query: MutableLiveData<String> = MutableLiveData()
    var pageId: MutableLiveData<Int> = MutableLiveData()

    fun setupSearchSubscription() {
        if (movies.value == null) {
            compositeDisposable.add(
                subject
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .map { Pair(it.first.trim(), it.second) }
                    .filter { query ->
                        when {
                            !checkInternetConnectionWithMessage() -> {
                                movies.postValue(Resource.empty(emptyList()))
                                return@filter false
                            }
                            query.first.isEmpty() -> {
                                movies.postValue(Resource.empty(emptyList()))
                                messageStringId.postValue(Resource.success())
                                return@filter false
                            }
                            else -> {
                                messageStringId.postValue(Resource.success())
                                return@filter true
                            }
                        }
                    }
                    .switchMap { query ->
                        Log.e("SearchFragmentViewModel", query.first)
                        loading.postValue(false)
                        moviesRepository.getMoviesWithSearchKeyword(query.first, query.second)
                    }
                    .distinctUntilChanged()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        Log.e("SearchFragmentViewModel", result.toString())
                        loading.postValue(false)
                        when {
                            RESPONSE_INVALID == result.response -> {
                                if (pageId.value == 1) {
                                    movies.postValue(Resource.empty(emptyList()))
                                    handleNetworkError(result.error)
                                }
                            }
                            result.data != null && result.data.isNotEmpty() -> {
                                if (pageId.value != null && pageId.value!! > 1) {
                                    movies.postValue(
                                        Resource.success(
                                            movies.value?.data?.plus(result.data) ?: result.data
                                        )
                                    )
                                } else {
                                    movies.postValue(Resource.success(result.data))
                                }
                                pageId.value = pageId.value?.plus(1)
                            }
                        }
                    }, {
                        loading.postValue(false)
                        movies.postValue(Resource.error(emptyList()))
                        handleNetworkError(it.message.toString())
                    })
            )
        }
    }

    private fun handleNetworkError(error: String?): Unit? {
        return error?.run {
            when (this) {
                TOO_MANY_RESULTS_ERROR -> messageStringId.postValue(Resource.error(R.string.too_many_results))
                MOVIE_NOT_FOUND_ERROR -> messageStringId.postValue(Resource.error(R.string.movie_not_found))
                else -> messageStringId.postValue(Resource.error(R.string.something_went_wrong))
            }
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

    fun updateQuery(query: String, pageId: Int) {
        this.pageId.value = pageId
        this.query.value = query
        subject.onNext(Pair(query, pageId))
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
            val index = movies?.indexOfFirst { info -> movieMetaInfo.imdbId == info.imdbId }
            val filterMovies = movies?.filterNot { info -> movieMetaInfo.imdbId == info.imdbId }
            if (filterMovies != null) {
                newMovies.addAll(filterMovies)
            }
            if (index != null) {
                newMovies.add(index, movieMetaInfo)
            }
            this.movies.postValue(Resource.success(newMovies))
        }
    }

    fun onLoadMore() {
        if (loading.value !== null && loading.value == false) loadMoreMovies()
    }

    private fun loadMoreMovies() {
        if (pageId.value != null && query.value != null && checkInternetConnectionWithMessage()) subject.onNext(
            Pair(query.value!!, pageId.value!!)
        )
    }

}

