package com.example.moviesearch.di.module

import androidx.lifecycle.LifecycleRegistry
import com.example.moviesearch.di.ViewModelScope
import com.example.moviesearch.ui.search.MovieItemViewHolder
import dagger.Module
import dagger.Provides

@Module
class ViewHolderModule(private val viewHolder: MovieItemViewHolder) {
    @Provides
    @ViewModelScope
    fun provideLifecycleRegistry(): LifecycleRegistry = LifecycleRegistry(viewHolder)
}
