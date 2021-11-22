package com.example.moviesearch.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MovieDetails(
    @Expose
    @SerializedName("Response")
    val response: String,

    @Expose
    @SerializedName("Title")
    var title: String,

    @Expose
    @SerializedName("Year")
    var year: String,

    @Expose
    @SerializedName("imdbID")
    val imdbId: String,

    @Expose
    @SerializedName("Poster")
    val poster: String,

    @Expose
    @SerializedName("Director")
    val director: String,

    @Expose
    @SerializedName("Plot")
    val plot: String,

    @Expose
    @SerializedName("imdbRating")
    val rating: String,

    @Expose
    @SerializedName("Error")
    var error: String,
)
