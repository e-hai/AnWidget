package com.anwidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import com.anwidget.video.VideoAdapter
import com.anwidget.video.VideoModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class MainActivity : AppCompatActivity() {

    val videoList = listOf(
        TestModel("1", createMediaSource("")),
        TestModel("2", createMediaSource("")),
        TestModel("3", createMediaSource("")),
        TestModel("4", createMediaSource("")),
        TestModel("5", createMediaSource("")),
        TestModel("6", createMediaSource("")),
        TestModel("7", createMediaSource("")),
        TestModel("8", createMediaSource("")),
        TestModel("9", createMediaSource("")),
        TestModel("10", createMediaSource(""))
    )

    private fun createMediaSource(path: String): MediaSource {
        return ProgressiveMediaSource
            .Factory(DefaultDataSourceFactory(this, this.packageName))
            .createMediaSource(MediaItem.fromUri(path))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}

data class TestModel(val title: String, override val mediaSource: MediaSource) :
    VideoModel(mediaSource)

class TestAdapter(lifecycleOwner: LifecycleOwner) :
    VideoAdapter<TestModel, TestAdapter.Companion.TestVH>(lifecycleOwner, COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_test, parent, false)
        return TestVH(view)
    }


    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<TestModel>() {
            override fun areItemsTheSame(
                oldItem: TestModel,
                newItem: TestModel
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: TestModel,
                newItem: TestModel
            ): Boolean {
                return oldItem == newItem
            }
        }

        class TestVH(itemView: View) : VideoViewHolder<TestModel>(itemView) {


            override fun bind(data: TestModel) {
            }

            override fun getPlayerView(): PlayerView {
                TODO("Not yet implemented")
            }

            override fun setCoverVisibility(visibility: Boolean) {

            }
        }
    }
}
