package com.example.moviesearch.data.response

import com.example.moviesearch.data.model.MovieMetaInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MovieSearchResponse(
    @Expose
    @SerializedName("Response")
    var response: String,

    @Expose
    @SerializedName("Error")
    var error: String? = null,

    @Expose
    @SerializedName("totalResults")
    var totalResults: String? = null,

    @Expose
    @SerializedName("Search")
    val data: List<MovieMetaInfo>? = null
)
