package com.amrtm.android.bcalc.component.data.repository.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.amrtm.android.bcalc.component.data.repository.Note
import com.amrtm.android.bcalc.component.data.repository.StorageNote
import java.util.Date

class PagingNoteSource(
    val notes: StorageNote,
    val query: String,
    val start: Date,
    val end: Date
): PagingSource<Int, Note>() {
    private var nameIndex: String = ""

    override fun getRefreshKey(state: PagingState<Int, Note>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Note> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = notes.getAllNotes(query,start,end,nextPageNumber.toLong(),nameIndex,params.loadSize)
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