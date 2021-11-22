package com.example.moviesearch.data.repository

import com.example.moviesearch.data.local.DatabaseService
import com.example.moviesearch.data.model.MovieDetails
import com.example.moviesearch.data.model.MovieMetaInfo
import com.example.moviesearch.data.remote.NetworkService
import com.example.moviesearch.data.response.MovieSearchResponse
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import java.util.*
import javax.inject.Inject

class MoviesRepository @Inject constructor(
    private val networkService: NetworkService,
    private val databaseService: DatabaseService
) {

    fun getAllFavoriteMoviesFromDb(): Single<List<MovieMetaInfo>> =
        databaseService.movieDao().getAllMovies()

    fun getMoviesWithSearchKeyword(
        searchKeyWord: String,
        pageId: Int?
    ): Flowable<MovieSearchResponse> {
        val moviesFromApi =
            networkService.getMoviesWithSearchKeyword(searchKeyWord = searchKeyWord, page = pageId)
        val moviesFromDb =
            databaseService.movieDao().getAllMoviesWithKeyword(searchKeyword = searchKeyWord)
        val moviesFromApiAndDb = Observable.zip(moviesFromApi, moviesFromDb,
            BiFunction<MovieSearchResponse, List<MovieMetaInfo>, MovieSearchResponse> { apiResponse, dbResponse ->
                val apiData = apiResponse.data
                if (pageId != null && pageId > 1) {
                    return@BiFunction apiResponse.copy(data = getMovieList(apiData, emptyList()))
                }
                return@BiFunction apiResponse.copy(data = getMovieList(apiData, dbResponse))
            })
        return moviesFromApiAndDb.toFlowable(BackpressureStrategy.LATEST)
    }

    private fun getMovieList(
        moviesFromApi: List<MovieMetaInfo>?,
        moviesFromDb: List<MovieMetaInfo>
    ): List<MovieMetaInfo> {
        val movies = HashSet(moviesFromDb)
        moviesFromApi?.forEach { movies.add(it) }
        return movies.toList()
    }

    fun getMovieDetails(imdbId: String): Observable<MovieDetails> =
        networkService.getMovieDetails(imdbId)

}
