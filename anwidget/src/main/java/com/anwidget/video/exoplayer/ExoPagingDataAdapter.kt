package com.anwidget.video.exoplayer

import android.view.View
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class ExoPagingDataAdapter<T : Any, VH : ExoPagingDataAdapter.VideoViewHolder>(
    private val videoManager: ExoManager,
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
            videoManager.playVideoFromMediaSource(
                getPlayerView(),
                getVideoMediaSource(position)
            )
        }
    }

    abstract fun getVideoMediaSource(position: Int): MediaSource?

    abstract class VideoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        abstract fun getPlayerView(): StyledPlayerView
    }
}