package com.anwidget.video.original

import android.net.Uri
import android.view.View
import android.widget.VideoView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class VideoViewAdapter<T : Any, VH : VideoViewAdapter.VideoViewHolder>(
    private val videoManager: VideoViewManager,
    diffCallback: DiffUtil.ItemCallback<T>,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default
) : PagingDataAdapter<T, VH>(diffCallback, mainDispatcher, workerDispatcher) {

    private var currentPosition = -1
    private lateinit var recyclerView: RecyclerView


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun switchPager(position: Int) {
        if (currentPosition == position) return
        (recyclerView.findViewHolderForAdapterPosition(position) as VH).apply {
            videoManager.playVideo(
                getPlayerView(),
                getVideoUri(position)
            )
        }
    }

    abstract fun getVideoUri(position: Int): Uri?

    abstract class VideoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        abstract fun getPlayerView(): VideoView
    }
}