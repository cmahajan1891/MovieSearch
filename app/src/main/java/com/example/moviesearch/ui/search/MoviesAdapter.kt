package com.example.moviesearch.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesearch.data.model.MovieMetaInfo
import com.example.moviesearch.databinding.ItemMovieBinding

class MoviesAdapter(
    parentLifecycle: Lifecycle,
    private val fragmentTag: String,
    private val callback: (movieMetaInfo: MovieMetaInfo) -> Unit
) : ListAdapter<MovieMetaInfo, MovieItemViewHolder>(MovieItemDiffCallback()) {

    private var recyclerView: RecyclerView? = null

    init {
        parentLifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onParentStart() {
                recyclerView?.let {
                    if (it.layoutManager is LinearLayoutManager) {
                        val firstVisibleItem =
                            (it.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                        val lastVisibleItem =
                            (it.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        if (firstVisibleItem in 0..lastVisibleItem) {
                            for (i in firstVisibleItem..lastVisibleItem) {
                                it.findViewHolderForAdapterPosition(i)?.let { viewHolder ->
                                    (viewHolder as MovieItemViewHolder).onStart()
                                }
                            }
                        }
                    }
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onParentStop() {
                recyclerView?.run {
                    for (i in 0 until childCount) {
                        getChildAt(i)?.let { view ->
                            (getChildViewHolder(view) as MovieItemViewHolder).onStop()
                        }
                    }
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onParentDestroy() {
                recyclerView?.run {
                    for (i in 0 until childCount) {
                        getChildAt(i)?.let { view ->
                            (getChildViewHolder(view) as MovieItemViewHolder).run {
                                onDestroy()
                                viewModel.onManualCleared()
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onViewAttachedToWindow(holder: MovieItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onStart()
    }

    override fun onViewDetachedFromWindow(holder: MovieItemViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onStop()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder =
        MovieItemViewHolder(
            ItemMovieBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            fragmentTag,
            callback
        )

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) =
        holder.bind(getItem(position))

}
