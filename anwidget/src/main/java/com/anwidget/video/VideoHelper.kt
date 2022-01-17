package com.anwidget.video

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

object VideoHelper {


    fun createMediaSource(context: Context, uri: Uri): MediaSource {
        val dataSourceFactory = DefaultDataSource.Factory(context.applicationContext)
        val mediaItem = MediaItem.fromUri(uri)
        return ProgressiveMediaSource
            .Factory(dataSourceFactory)
            .createMediaSource(mediaItem)
    }
}