package com.amrtm.android.bcalc.component.data.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun add(data: Any): Int
    suspend fun update(data: Any): Int
    suspend fun delete(id: Any)
}