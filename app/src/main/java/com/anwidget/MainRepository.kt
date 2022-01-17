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
            try {
                val nextPageNumber = params.key ?: 1
                val responseData = createMediaSource()
                return LoadResult.Page(
                    data = responseData,
                    prevKey = null,
                    nextKey = nextPageNumber.plus(1)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return LoadResult.Error(e)
            }
        }
    }


    private fun createMediaSource(): List<String> {
        val dataList = mutableListOf<String>()
        for (i in 0..5) {
            dataList.add(i.toString())
        }
        return dataList
    }
}

