package com.anwidget

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anwidget.video.VideoAdapter
import com.anwidget.video.VideoManager
import com.anwidget.video.VideoView
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val adapter = TestAdapter(VideoManager(MyApp.application, this))
        recyclerview.adapter = adapter

        viewModel.getTestData().observe(this) {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        }
        findViewById<Button>(R.id.nextView).setOnClickListener {
            startActivity(Intent(this, SinglePlayActivity::class.java))
        }
    }
}


class TestAdapter(videoManager: VideoManager) :
    VideoAdapter<TestModel, TestAdapter.Companion.TestVH>(videoManager, COMPARATOR) {


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
                itemView.findViewById<TextView>(R.id.titleView).text = data.title
                val coverView = getPlayerView().coverView
                Glide.with(coverView)
                    .load(data.cover)
                    .into(coverView)
            }

            override fun getPlayerView(): VideoView {
                return itemView.findViewById(R.id.videoView)
            }
        }
    }
}
