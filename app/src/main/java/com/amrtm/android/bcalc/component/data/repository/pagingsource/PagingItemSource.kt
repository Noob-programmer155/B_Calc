package com.amrtm.android.bcalc.component.data.repository.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.amrtm.android.bcalc.component.data.repository.Item
import com.amrtm.android.bcalc.component.data.repository.StorageItem
import java.math.BigDecimal

class PagingItemSourceFilter(
    val items: StorageItem,
    val query: String,
    val start: BigDecimal,
    val end: BigDecimal
): PagingSource<Int, Item>() {
    private var nameIndex: String = ""

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = items.getItems(query,start,end,nextPageNumber.toLong(),nameIndex,params.loadSize)
            Log.i("SIZE","query: ${query}, start: ${start.toPlainString()}, end: ${end.toPlainString()} " +
                    ",loadsize: ${params.loadSize} ,nameIndex: ${nameIndex} ,nextPage: ${nextPageNumber.toLong()} ,response: ${response.size}")
            return if (response.isNotEmpty()) {
                nameIndex = response.last().name
                LoadResult.Page(
                    data = response,
                    prevKey = null, // Only paging forward.
                    nextKey = response.last().id?.toInt()
                )
            } else
                LoadResult.Page(
                    data = listOf(),
                    prevKey = null, // Only paging forward.
                    nextKey = null
                )
        } catch (e: Exception) {
            Log.e("LOAD",e.message!!)
            LoadResult.Error(e)
        }
    }
}

class PagingItemSource(val items: StorageItem): PagingSource<Int, Item>() {
    var nameIndex: String = ""

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = items.getItems(nextPageNumber.toLong(),nameIndex,params.loadSize)
            Log.i("SIZE","loadsize: ${params.loadSize} ,nameIndex: ${nameIndex} ,nextPage: ${nextPageNumber.toLong()} ,response: ${response.size}")
            if (response.isNotEmpty()) {
                nameIndex = response.last().name
                LoadResult.Page(
                    data = response,
                    prevKey = null, // Only paging forward.
                    nextKey = response.last().id?.toInt()
                )
            } else
                LoadResult.Page(
                    data = listOf(),
                    prevKey = null, // Only paging forward.
                    nextKey = null
                )
        } catch (e: Exception) {
            Log.e("LOAD",e.message!!)
            LoadResult.Error(e)
        }
    }
}