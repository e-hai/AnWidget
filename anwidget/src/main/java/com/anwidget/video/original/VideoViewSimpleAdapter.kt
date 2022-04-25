package com.anwidget.video.original

import android.net.Uri
import android.view.View
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView

abstract class VideoViewSimpleAdapter<VH : VideoViewSimpleAdapter.VideoViewSimpleHolder>(
    private val videoManager: VideoViewManager
) : RecyclerView.Adapter<VH>() {

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

    abstract class VideoViewSimpleHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        abstract fun getPlayerView(): VideoView
    }
}