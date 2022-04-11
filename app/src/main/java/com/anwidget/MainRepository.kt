package com.anwidget

import androidx.paging.*
import kotlinx.coroutines.flow.Flow

object MainRepository {

    fun getTestPaging(): Flow<PagingData<String>> {
        return Pager(PagingConfig(10)) {
            ListPagingSource()
        }.flow
    }

    class ListPagingSource : PagingSource<Int, String>() {

        override fun getRefreshKey(state: PagingState<Int, String>): Int? {
            return null
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
            return try {
                val nextPageNumber = params.key ?: 1
                val responseData = createMediaSource()
                LoadResult.Page(
                    data = responseData,
                    prevKey = null,
                    nextKey = nextPageNumber.plus(1)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                LoadResult.Error(e)
            }
        }
    }


    private fun createMediaSource(): List<String> {
        return listOf(
            "http://face-model-osszh.startech.ltd/basis-admin/6c1405e63dab4a4d933cf4c6d86d712d.mp4",
            "http://face-model-osszh.startech.ltd/basis-admin/50d5ed442c4444baafb1f238e21922e4.mp4",
            "http://face-model-osszh.startech.ltd/new_result/e8cdab05f246442b8752e1ae22e2667c.mp4",
            "http://face-model-osszh.startech.ltd/basis-admin/620ba816fbe04380afa1965e1d7da8ad.mp4",
            "http://face-model-osszh.startech.ltd/basis-admin/9ffa041c001a462f9d31acb340c09bf3.mp4",
            "http://face-model-osszh.startech.ltd/basis-admin/50a4913336ce4ea6b3f3979ce4668cd6.mp4"
        )
    }

}

