package com.example.moviesearch.ui.search

import androidx.recyclerview.widget.DiffUtil
import com.example.moviesearch.data.model.MovieMetaInfo

class MovieItemDiffCallback : DiffUtil.ItemCallback<MovieMetaInfo>() {
    override fun areItemsTheSame(oldItem: MovieMetaInfo, newItem: MovieMetaInfo): Boolean {
        return oldItem.imdbId == newItem.imdbId
    }

    override fun areContentsTheSame(oldItem: MovieMetaInfo, newItem: MovieMetaInfo): Boolean {
        return oldItem == newItem
    }

}
