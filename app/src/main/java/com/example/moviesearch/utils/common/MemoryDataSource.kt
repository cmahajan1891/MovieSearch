package com.example.moviesearch.utils.common

import android.util.LruCache
import com.example.moviesearch.data.model.MovieDetails
import io.reactivex.Observable
import javax.inject.Singleton

@Singleton
class MemoryDataSource(maxSize: Int) : LruCache<String, MovieDetails>(maxSize) {

    fun addItem(key: String, movieDetails: MovieDetails) {
        this.put(key, movieDetails)
    }

    fun getItem(key: String): Observable<MovieDetails> {
        return Observable.create { emitter ->
            val movieDetails = this.get(key)
            if (movieDetails != null) {
                emitter.onNext(movieDetails)
            }
            emitter.onComplete()
        }
    }
}
