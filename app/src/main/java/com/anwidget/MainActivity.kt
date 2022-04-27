package com.anwidget

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.widget.ViewPager2
import com.anwidget.video.exoplayer.ExoManager
import com.anwidget.video.exoplayer.ExoPagingDataAdapter
import com.anwidget.video.original.VideoViewPagingAdapter
import com.anwidget.video.original.VideoViewManager
import com.anwidget.video.original.VideoViewSimpleAdapter
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = OriginalDemoAdapter(VideoViewManager(this))
        findViewById<ViewPager2>(R.id.recyclerview).apply {
            this.adapter = adapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    adapter.switchPager(position)
                }
            })
        }

        viewModel.getPagingDataFromString().observe(this) {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        }
        findViewById<Button>(R.id.nextView).setOnClickListener {
            startActivity(Intent(this, SinglePlayActivity::class.java))
        }
    }
}


class ExoDemoAdapter(videoManager: ExoManager) : ExoPagingDataAdapter<TestModelExo,
        ExoDemoAdapter.ExoVH>(videoManager, COMPARATOR) {
    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<TestModelExo>() {
            override fun areItemsTheSame(
                oldItem: TestModelExo,
                newItem: TestModelExo
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: TestModelExo,
                newItem: TestModelExo
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExoVH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_exo_demo, parent, false)
        return ExoVH(view)
    }

    override fun onBindViewHolder(holder: ExoVH, position: Int) {
    }

    class ExoVH(itemView: View) : ExoPagingDataAdapter.VideoViewHolder(itemView) {

        override fun getPlayerView(): StyledPlayerView {
            return itemView.findViewById(R.id.videoView)
        }
    }


    override fun getVideoMediaSource(position: Int): MediaSource? {
        return getItem(position)?.source
    }
}

class OriginalDemoAdapter(videoManager: VideoViewManager) :
    VideoViewPagingAdapter<String, OriginalDemoAdapter.OriginalVH>(videoManager, ORIGINAL_COMPARATOR) {

    companion object {
        val ORIGINAL_COMPARATOR = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OriginalVH {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_videoview_demo, parent, false)
        return OriginalVH(view)
    }

    override fun onBindViewHolder(holder: OriginalVH, position: Int) {
    }

    class OriginalVH(itemView: View) : VideoViewPagingHolder(itemView) {
        override fun getPlayerView(): VideoView {
            return itemView.findViewById(R.id.videoView)
        }
    }

    override fun getVideoUri(position: Int): Uri? {
        return getItem(position)?.toUri()
    }
}

class OriginalSimpleAdapter(videoManager: VideoViewManager) :
    VideoViewSimpleAdapter<OriginalSimpleAdapter.SimpleVH>(videoManager) {
    private val dataList = emptyList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleVH {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_videoview_demo, parent, false)
        return SimpleVH(view)
    }

    override fun onBindViewHolder(holder: SimpleVH, position: Int) {
    }

    class SimpleVH(itemView: View) : VideoViewSimpleHolder(itemView) {
        override fun getPlayerView(): VideoView {
            return itemView.findViewById(R.id.videoView)
        }
    }

    override fun getVideoUri(position: Int): Uri {
        return dataList[position].toUri()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
