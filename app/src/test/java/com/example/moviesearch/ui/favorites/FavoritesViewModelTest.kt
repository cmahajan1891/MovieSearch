package com.example.moviesearch.ui.favorites

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.moviesearch.data.model.MovieMetaInfo
import com.example.moviesearch.data.repository.MoviesRepository
import com.example.moviesearch.rx.TestSchedulerProvider
import com.example.moviesearch.utils.common.Resource
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FavoritesViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var moviesRepository: MoviesRepository

    @Mock
    private lateinit var moviesObserver: Observer<Resource<List<MovieMetaInfo>>>

    private lateinit var testScheduler: TestScheduler

    private lateinit var favoritesViewModel: FavoritesViewModel

    @Test
    fun givenDbResponse200_whenGetFavorite_shouldShowMovies() {
        val movies = listOf(
            MovieMetaInfo(
                "The Social Network",
                "2010",
                "tt1285016",
                "https://m.media-amazon.com/images/M/MV5BOGUyZDUxZjEtMmIzMC00MzlmLTg4MGItZWJmMzBhZjE0Mjc1XkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_SX300.jpg",
                true
            ),
            MovieMetaInfo(
                "The Social Dilemma",
                "2020",
                "tt11464826",
                "https://m.media-amazon.com/images/M/MV5BNDVhMGNhYjEtMDkwZi00NmQ5LWFkODktYzhiYjY2NTZmYTNhXkEyXkFqcGdeQXVyMTkxNjUyNQ@@._V1_SX300.jpg",
                true
            )
        )

        val compositeDisposable = CompositeDisposable()
        testScheduler = TestScheduler()
        val testSchedulerProvider = TestSchedulerProvider(testScheduler)

        Mockito.`when`(moviesRepository.getAllFavoriteMoviesFromDb()).thenReturn(
            Single.just(movies)
        )

        favoritesViewModel = FavoritesViewModel(
            testSchedulerProvider,
            compositeDisposable,
            moviesRepository
        )

        favoritesViewModel.movies.observeForever(moviesObserver)
        testScheduler.triggerActions()

        assert(favoritesViewModel.movies.value == Resource.success(movies))
        verify(moviesObserver).onChanged(Resource.success(movies))

    }

}
