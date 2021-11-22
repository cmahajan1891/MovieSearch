package com.example.moviesearch.data.remote

import com.example.moviesearch.data.model.MovieDetails
import com.example.moviesearch.data.response.MovieSearchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface NetworkService {

    @GET("/")
    fun getMoviesWithSearchKeyword(
        @Query("s") searchKeyWord: String,
        @Query("type") type: String = "movie",
        @Query("page") page: Int? = 1,
        @Query(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Observable<MovieSearchResponse>

    @GET("/")
    fun getMovieDetails(
        @Query("i") imdbId: String,
        @Query(Networking.HEADER_API_KEY) apiKey: String = Networking.API_KEY
    ): Observable<MovieDetails>

}
