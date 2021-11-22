package com.example.moviesearch.di.component

import com.example.moviesearch.di.FragmentScope
import com.example.moviesearch.di.module.FragmentModule
import com.example.moviesearch.ui.favorites.FavoritesFragment
import com.example.moviesearch.ui.moviedetail.MovieDetailFragment
import com.example.moviesearch.ui.search.SearchFragment
import dagger.Component

@FragmentScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [FragmentModule::class]
)
interface FragmentComponent {
    fun inject(fragment: SearchFragment)
    fun inject(fragment: MovieDetailFragment)
    fun inject(fragment: FavoritesFragment)
}
