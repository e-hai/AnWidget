package com.anwidget.video

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.*
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class VideoAdapter<T : VideoModel, VH : VideoAdapter.VideoViewHolder<T>>(
    private val videoManager: VideoManager,
    diffCallback: DiffUtil.ItemCallback<T>,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default
) : PagingDataAdapter<T, VH>(diffCallback, mainDispatcher, workerDispatcher) {

    private var lastVH: VH? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        PagerSnapHelper().attachToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (0 == dx && 0 == dy) {
                    switchPager(recyclerView)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        switchPager(recyclerView)
                    }
                }
            }
        })
    }

    private fun switchPager(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val visiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition().also {
            if (it < 0) return
        }
        val item = getItem(visiblePosition) ?: return
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(visiblePosition) as VH
        if (lastVH == viewHolder) {
            return
        }
        lastVH = viewHolder.apply {
            videoManager.playVideoFromMediaSource(
                getPlayerView(),
                item.mediaSource
            )
        }
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    abstract class VideoViewHolder<T : VideoModel>(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        abstract fun getPlayerView(): VideoView

        abstract fun bind(data: T)
    }
}