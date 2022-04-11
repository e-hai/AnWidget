package com.anwidget.video.original

import android.net.Uri
import android.widget.VideoView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class VideoViewManager(
    lifecycleOwner: LifecycleOwner
) {

    private var currentVideoView: VideoView? = null

    init {
        val lifecycleEventObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    currentVideoView?.start()
                }
                Lifecycle.Event.ON_RESUME -> {
                    currentVideoView?.resume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    currentVideoView?.pause()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    currentVideoView?.stopPlayback()
                }
                else -> {
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleEventObserver)
    }


    fun playVideo(videoView: VideoView, videoUri: Uri?) {
        currentVideoView?.stopPlayback()
        videoView.setOnErrorListener { mp, what, extra -> true }
        videoView.setOnCompletionListener {
            videoView.start()
        }
        videoView.setVideoURI(videoUri)
        videoView.start()
        currentVideoView = videoView
    }
}