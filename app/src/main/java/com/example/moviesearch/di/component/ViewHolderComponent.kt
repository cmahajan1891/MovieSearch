package com.example.moviesearch.di.component

import com.example.moviesearch.di.ViewModelScope
import com.example.moviesearch.di.module.ViewHolderModule
import com.example.moviesearch.ui.search.MovieItemViewHolder
import dagger.Component

@ViewModelScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [ViewHolderModule::class]
)
interface ViewHolderComponent {
    fun inject(viewHolder: MovieItemViewHolder)
}
