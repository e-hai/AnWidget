package com.anwidget

import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import androidx.paging.map
import com.anwidget.video.VideoHelper.createMediaSource
import com.anwidget.video.VideoModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.flow.map

class MainViewModel(private val context: Application) : AndroidViewModel(context) {


    fun getTestData(): LiveData<PagingData<TestModel>> {
        return MainRepository.getTestPaging()
            .map { data ->
                data.map {
                    TestModel(
                        it,
                        "https://pic3.pocoimg.cn/image/poco/works/61/2021/0901/08/16304575486922620_179094558.jpg?imageMogr2/auto-orient/thumbnail/120x/blur/1x0/quality/100&",
                        createMediaSource(context, responseData[it]?.toUri() ?: "".toUri())
                    )
                }
            }.asLiveData()
    }

    private val responseData = mapOf(
        Pair(
            "0",
            "http://face-model-osszh.startech.ltd/basis-admin/6c1405e63dab4a4d933cf4c6d86d712d.mp4"
        ),
        Pair(
            "1",
            "http://face-model-osszh.startech.ltd/basis-admin/50d5ed442c4444baafb1f238e21922e4.mp4"
        ),
        Pair(
            "2",
            "http://face-model-osszh.startech.ltd/new_result/e8cdab05f246442b8752e1ae22e2667c.mp4"
        ),
        Pair(
            "3",
            "http://face-model-osszh.startech.ltd/basis-admin/620ba816fbe04380afa1965e1d7da8ad.mp4"
        ),
        Pair(
            "4",
            "http://face-model-osszh.startech.ltd/basis-admin/9ffa041c001a462f9d31acb340c09bf3.mp4"
        ),
        Pair(
            "5",
            "http://face-model-osszh.startech.ltd/basis-admin/50a4913336ce4ea6b3f3979ce4668cd6.mp4"
        )
    )
}

data class TestModel(val title: String, val cover: String, val source: MediaSource) :
    VideoModel(source)