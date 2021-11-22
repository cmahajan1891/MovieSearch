package com.example.moviesearch.di.component

import com.example.moviesearch.MovieSearchApplication
import com.example.moviesearch.data.local.DatabaseService
import com.example.moviesearch.data.remote.NetworkService
import com.example.moviesearch.di.module.ApplicationModule
import com.example.moviesearch.utils.common.MemoryDataSource
import com.example.moviesearch.utils.network.NetworkHelper
import com.example.moviesearch.utils.rx.SchedulerProvider
import dagger.Component
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(app: MovieSearchApplication)

    fun getNetworkService(): NetworkService

    fun getDatabaseService(): DatabaseService

    fun getNetworkHelper(): NetworkHelper

    /**---------------------------------------------------------------------------
     * Dagger will internally create UserRepository instance using constructor injection.
     * Dependency through constructor
     * UserRepository ->
     *  [NetworkService -> Nothing is required],
     *  [DatabaseService -> Nothing is required],
     *  [UserPreferences -> [SharedPreferences -> provided by the function provideSharedPreferences in ApplicationModule class]]
     * So, Dagger will be able to create an instance of UserRepository by its own using constructor injection
     *---------------------------------------------------------------------------------
     */

    fun getSchedulerProvider(): SchedulerProvider

    fun getCompositeDisposable(): CompositeDisposable

    fun getMemoryDataSource(): MemoryDataSource

}
