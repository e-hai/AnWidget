package com.anwidget.video

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.annotation.RawRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.RawResourceDataSource

class VideoManager(
    private val context: Context,
    lifecycleOwner: LifecycleOwner,
    private val loop: Boolean = true
) {

    private var playerView: VideoView? = null
    private var mediaSource: MediaSource? = null
    private var player: ExoPlayer? = null


    private fun createPlayer(context: Context) = ExoPlayer
        .Builder(context)
        .build()
        .apply {
            playWhenReady = true
        }

    init {
        Log.d(TAG, "init")
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    Log.d(TAG, "ON_START")
                    player = createPlayer(context).apply {
                        repeatMode = if (loop) {
                            Player.REPEAT_MODE_ONE
                        } else {
                            Player.REPEAT_MODE_OFF
                        }
                    }
                    playerView?.player = player
                    mediaSource?.apply {
                        player?.setMediaSource(this)
                        player?.prepare()
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    Log.d(TAG, "ON_RESUME")
                    playerView?.onResume()
                    player?.playWhenReady = true
                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d(TAG, "ON_PAUSE")
                    playerView?.onPause()
                    player?.playWhenReady = false
                }
                Lifecycle.Event.ON_STOP -> {
                    Log.d(TAG, "ON_STOP")
                    playerView?.player = null
                    player?.stop()
                    player?.release()
                }
                else -> {
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    }

    fun playVideoFromRaw(
        playerView: VideoView,
        @RawRes videoRaw: Int
    ) {
        val context = playerView.context
        val rawResourceDataSource = RawResourceDataSource(context)
        val dataSpec = DataSpec(RawResourceDataSource.buildRawResourceUri(videoRaw))
        try {
            rawResourceDataSource.open(dataSpec)
        } catch (e: RawResourceDataSource.RawResourceDataSourceException) {
            e.printStackTrace()
        }
        val videoUri = rawResourceDataSource.uri ?: return
        simplePlay(context, playerView, videoUri)
    }

    fun playVideoFromUrl(
        playerView: VideoView,
        videoUrl: String
    ) {
        val context = playerView.context
        val videoUri = Uri.parse(videoUrl)
        simplePlay(context, playerView, videoUri)
    }

    private fun simplePlay(
        context: Context,
        playerView: VideoView,
        videoUri: Uri
    ) {
        playerView.useController = false
        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        val mediaSource = VideoHelper.createMediaSource(context,videoUri)
        playVideoFromMediaSource(playerView, mediaSource)
    }

    fun playVideoFromMediaSource(
        newPlayerView: VideoView,
        newMediaSource: MediaSource
    ) {
        playerView?.let {
            it.player = null
        }
        newPlayerView.player = player?.apply {
            setMediaSource(newMediaSource)
            prepare()
        }
        mediaSource = newMediaSource
        playerView = newPlayerView
    }

    companion object {
        const val TAG = "VideoManager"
    }
}