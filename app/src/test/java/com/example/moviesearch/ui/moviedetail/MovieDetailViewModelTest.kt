package com.example.moviesearch.ui.moviedetail

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.moviesearch.R
import com.example.moviesearch.data.model.MovieDetails
import com.example.moviesearch.data.repository.MoviesRepository
import com.example.moviesearch.rx.TestSchedulerProvider
import com.example.moviesearch.utils.common.MemoryDataSource
import com.example.moviesearch.utils.common.Resource
import com.example.moviesearch.utils.network.NetworkHelper
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ExampleUnitTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var movieDetailObserver: Observer<MovieDetails>

    @Mock
    private lateinit var progressBarObserver: Observer<Boolean>

    @Mock
    private lateinit var messageStringIdObserver: Observer<Resource<Int>>

    private lateinit var testScheduler: TestScheduler
    private lateinit var movieDetailViewModel: MovieDetailViewModel
    private lateinit var testSchedulerProvider: TestSchedulerProvider

    @Mock
    lateinit var networkHelper: NetworkHelper

    @Mock
    lateinit var memoryDataSource: MemoryDataSource

    @Mock
    lateinit var moviesRepository: MoviesRepository

    @Test
    fun getServerResponse200_whenViewDetail_shouldLaunchDetailViewFragment() {

        val movieDetailResponse = MovieDetails(
            response = "True",
            title = "The Social Network",
            "2010",
            "tt1285016",
            "",
            "David Fincher",
            "As Harvard student Mark Zuckerberg creates the social networking site that would become known as Facebook, he is sued by the twins who claimed he stole their idea, and by the co-founder who was later squeezed out of the business.",
            "Ratings: [\n" +
                    "{\n" +
                    "Source: \"Internet Movie Database\",\n" +
                    "Value: \"7.7/10\"\n" +
                    "},\n" +
                    "{\n" +
                    "Source: \"Rotten Tomatoes\",\n" +
                    "Value: \"96%\"\n" +
                    "},\n" +
                    "{\n" +
                    "Source: \"Metacritic\",\n" +
                    "Value: \"95/100\"\n" +
                    "}\n" +
                    "],",
            error = ""
        )

        `when`(networkHelper.isNetworkConnected()).thenReturn(
            true
        )

        `when`(memoryDataSource.getItem("tt1285016")).thenReturn(Observable.create { emitter ->
            emitter.onComplete()
        })

        `when`(moviesRepository.getMovieDetails("tt1285016")).thenReturn(
            Observable.just(movieDetailResponse)
        )

        testScheduler = TestScheduler()
        val compositeDisposable = CompositeDisposable()
        testSchedulerProvider = TestSchedulerProvider(testScheduler)

        movieDetailViewModel = MovieDetailViewModel(
            memoryDataSource = memoryDataSource,
            imdbId = "tt1285016",
            schedulerProvider = testSchedulerProvider,
            compositeDisposable = compositeDisposable,
            moviesRepository = moviesRepository,
            networkHelper = networkHelper
        )

        movieDetailViewModel.movieDetails.observeForever(movieDetailObserver)
        movieDetailViewModel.progressBarShown.observeForever(progressBarObserver)

        testScheduler.triggerActions()

        assert(movieDetailViewModel.movieDetails.value == movieDetailResponse)
        assert(movieDetailViewModel.progressBarShown.value == false)
        verify(memoryDataSource).addItem("tt1285016", movieDetailResponse)

        movieDetailViewModel.movieDetails.removeObserver(movieDetailObserver)
        movieDetailViewModel.progressBarShown.removeObserver(progressBarObserver)
    }

    @Test
    fun givenNoInternet_whenViewDetails_shouldShowNetworkError() {
        Mockito.doReturn(false)
            .`when`(networkHelper)
            .isNetworkConnected()

        testScheduler = TestScheduler()
        val compositeDisposable = CompositeDisposable()
        testSchedulerProvider = TestSchedulerProvider(testScheduler)

        movieDetailViewModel = MovieDetailViewModel(
            memoryDataSource = memoryDataSource,
            imdbId = "tt1285016",
            schedulerProvider = testSchedulerProvider,
            compositeDisposable = compositeDisposable,
            moviesRepository = moviesRepository,
            networkHelper = networkHelper
        )

        movieDetailViewModel.messageStringId.observeForever(messageStringIdObserver)

        testScheduler.triggerActions()

        assert(movieDetailViewModel.messageStringId.value == Resource.error(R.string.network_connection_error))
        verify(messageStringIdObserver).onChanged(Resource.error(R.string.network_connection_error))

        movieDetailViewModel.messageStringId.removeObserver(messageStringIdObserver)
    }

}
