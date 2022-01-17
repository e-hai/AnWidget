package com.anwidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anwidget.video.VideoManager
import com.anwidget.video.VideoView

class SinglePlayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_play)
        val videoView = findViewById<VideoView>(R.id.videoView)
        VideoManager(this, this).playVideoFromUrl(videoView, "http://face-model-osszh.startech.ltd/basis-admin/50d5ed442c4444baafb1f238e21922e4.mp4")
    }
}