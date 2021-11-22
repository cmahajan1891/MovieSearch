package com.example.moviesearch.ui.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviesearch.data.local.DatabaseService
import com.example.moviesearch.data.model.MovieMetaInfo
import com.example.moviesearch.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MovieItemViewModel @Inject constructor(
    private val schedulerProvider: SchedulerProvider,
    private val databaseService: DatabaseService,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    companion object {
        const val TAG = "MovieItemViewModel"
    }

    val data: MutableLiveData<MovieMetaInfo> = MutableLiveData()
    val isFavStatusChanged: MutableLiveData<Boolean> = MutableLiveData(false)

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun updateData(data: MovieMetaInfo) {
        this.data.postValue(data)
    }

    fun onManualCleared() = onCleared()

    fun onFavoriteClick(movieItem: MovieMetaInfo) {
        val record = movieItem.copy(isFav = !movieItem.isFav)
        if (record.isFav) {
            compositeDisposable.add(
                databaseService
                    .movieDao()
                    .insert(record)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe(
                        {
                            Log.e(TAG, "Movie added as favorite in db $it")
                            updateData(record)
                            isFavStatusChanged.postValue(true)
                        },
                        {
                            Log.e(TAG, it.toString())
                        }
                    )
            )
        } else {
            compositeDisposable.add(
                databaseService
                    .movieDao()
                    .delete(record)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe(
                        {
                            Log.e(TAG, "Movie deleted from db $it")
                            updateData(record)
                            isFavStatusChanged.postValue(true)
                        },
                        {
                            Log.e(TAG, it.toString())
                        }
                    )
            )
        }
    }

    fun itemUpdated() {
        isFavStatusChanged.postValue(false)
    }

}
