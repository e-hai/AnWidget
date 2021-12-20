package com.anwidget.video

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class VideoAdapter<T : VideoModel, VH : VideoAdapter.VideoViewHolder<T>>(
    lifecycleOwner: LifecycleOwner,
    diffCallback: DiffUtil.ItemCallback<T>,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    workerDispatcher: CoroutineDispatcher = Dispatchers.Default
) : PagingDataAdapter<T, VH>(diffCallback, mainDispatcher, workerDispatcher) {

    private var player: SimpleExoPlayer? = null
    private var lastVH: VH? = null

    init {
        lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    lastVH?.onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    lastVH?.onPause()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    player?.stop()
                    player?.release()
                    player = null
                }
                else -> {
                }
            }
        })
        player?.setVideoFrameMetadataListener { presentationTimeUs, releaseTimeNs, format, mediaFormat ->
            lastVH?.setCoverVisibility(false)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
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
        val player = player ?: return
        val item = getItem(visiblePosition) ?: return
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(visiblePosition) as VH
        if (lastVH == viewHolder) {
            return
        } else {
            lastVH?.unbindPlayer()
            viewHolder.bindPlayer(player)
            lastVH = viewHolder
        }
        player.setMediaSource(item.mediaSource)
        player.prepare()
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    abstract class VideoViewHolder<T : VideoModel>(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        abstract fun getPlayerView(): PlayerView

        abstract fun bind(data: T)

        open fun setCoverVisibility(visibility: Boolean) {

        }

        fun bindPlayer(player: Player) {
            getPlayerView().visibility
            getPlayerView().player = player
            getPlayerView().useArtwork
        }

        fun unbindPlayer() {
            getPlayerView().player?.stop()
            getPlayerView().player = null
        }

        fun onResume() {
            getPlayerView().onResume()
            getPlayerView().player?.playWhenReady = true
        }

        fun onPause() {
            getPlayerView().onPause()
            getPlayerView().player?.playWhenReady = false
        }
    }
}