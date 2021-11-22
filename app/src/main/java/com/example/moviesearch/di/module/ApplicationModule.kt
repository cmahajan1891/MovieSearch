package com.example.moviesearch.di.module

import androidx.room.Room
import com.example.moviesearch.BuildConfig
import com.example.moviesearch.MovieSearchApplication
import com.example.moviesearch.data.local.DatabaseService
import com.example.moviesearch.data.remote.NetworkService
import com.example.moviesearch.data.remote.Networking
import com.example.moviesearch.utils.common.MemoryDataSource
import com.example.moviesearch.utils.network.NetworkHelper
import com.example.moviesearch.utils.rx.RxSchedulerProvider
import com.example.moviesearch.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: MovieSearchApplication) {

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider = RxSchedulerProvider()

    @Provides
    @Singleton
    fun provideDatabaseService(): DatabaseService =
        Room.databaseBuilder(
            application, DatabaseService::class.java,
            "movies-search-project-db"
        ).build()

    @Provides
    @Singleton
    fun provideNetworkService(): NetworkService =
        Networking.create(
            BuildConfig.API_KEY,
            BuildConfig.BASE_URL,
            application.cacheDir,
            10485760L, // 10MB,
            RxSchedulerProvider()
        )

    @Singleton
    @Provides
    fun provideNetworkHelper(): NetworkHelper = NetworkHelper(application)

    @Provides
    @Singleton
    fun providesMemoryDataSource(): MemoryDataSource = MemoryDataSource(10)
}
