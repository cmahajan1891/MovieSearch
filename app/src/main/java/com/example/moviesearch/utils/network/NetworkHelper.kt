package com.example.moviesearch.utils.network

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Singleton

@Singleton
class NetworkHelper constructor(private val context: Context) {

    companion object {
        private const val TAG = "NetworkHelper"
        const val TOO_MANY_RESULTS_ERROR = "Too many results."
        const val MOVIE_NOT_FOUND_ERROR = "Movie not found!"
        const val GETTING_DATA_ERROR = "Error getting data."
        const val RESPONSE_INVALID = "False"
    }

    fun isNetworkConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork?.isConnected ?: false
    }

}
