package com.example.moviesearch.data.remote

import com.example.moviesearch.utils.rx.SchedulerProvider
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object Networking {

    const val HEADER_API_KEY = "apikey"
    private const val NETWORK_CALL_TIMEOUT = 60
    internal lateinit var API_KEY: String

    fun create(
        apiKey: String,
        baseUrl: String,
        cacheDir: File,
        cacheSize: Long,
        schedulerProvider: SchedulerProvider
    ): NetworkService {
        API_KEY = apiKey
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(
                OkHttpClient.Builder()
                    .cache(Cache(cacheDir, cacheSize))
                    .readTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(schedulerProvider.io()))
            .build()
            .create(NetworkService::class.java)
    }
}
