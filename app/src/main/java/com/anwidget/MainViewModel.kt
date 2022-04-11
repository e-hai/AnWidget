package com.anwidget

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import androidx.paging.map
import com.anwidget.video.exoplayer.ExoHelper.createMediaSource
import com.google.android.exoplayer2.source.MediaSource
import kotlinx.coroutines.flow.map

class MainViewModel(private val context: Application) : AndroidViewModel(context) {

    fun getPagingData(): LiveData<PagingData<TestModelExo>> {
        return MainRepository.getTestPaging()
            .map { data ->
                data.map {
                    TestModelExo(createMediaSource(context, it.toUri()))
                }
            }.asLiveData()
    }

    fun getPagingDataFromString(): LiveData<PagingData<String>> {
        return MainRepository.getTestPaging()
            .asLiveData()
    }
}

data class TestModelExo(val source: MediaSource)