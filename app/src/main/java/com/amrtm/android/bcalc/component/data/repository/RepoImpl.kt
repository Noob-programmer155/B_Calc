package com.amrtm.android.bcalc.component.data.repository

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.Date

class BalanceRepo(private val main: StorageBalance): Repository {
    fun get(): Flow<Balance> = main.getBalance()
    override suspend fun add(data: Any) = main.insertBalance(data as Balance)
    override suspend fun update(data: Any) = main.updateBalance(data as Balance)
    override suspend fun delete(id: Any) {}
}

class NoteRepo(private val main: StorageNote): Repository {
    fun getAll(name: String, pageSize: Int, dateStart: Date, dateEnd: Date): Flow<PagingData<Note>> = Pager(
        PagingConfig(pageSize = pageSize)
    ) {
        main.getAllNotes(name,dateStart,dateEnd)
    }.flow
    fun get(id: Int): Flow<Map<Note, List<Item>>> = main.getAllItemNotes(id)
    override suspend fun add(data: Any) = main.insertNote(data as Note)
    override suspend fun update(data: Any) = main.updateNote(data as Note)
    override suspend fun delete(id: Any) = main.deleteNote(id as Int)
}

class ItemRepo(private val main: StorageItem): Repository {
    fun getAllItems(name: String, pageSize: Int, start: BigDecimal, end: BigDecimal): Flow<PagingData<Item>> = Pager(
        PagingConfig(pageSize = pageSize)
    ) {
        main.getGroupingItems(name,start,end)
    }.flow
    fun get(name: String): Flow<List<Item>> = main.getHistoryItem(name)
    override suspend fun add(data: Any) = main.insertItem(data as Item)
    suspend fun add(data: List<Item>) = main.insertItem(data)
    override suspend fun update(data: Any) = main.updateItem(data as Item)
    suspend fun update(data: List<Item>) = main.updateItem(data)
    override suspend fun delete(id: Any) = main.deleteItem(id as Int)
    suspend fun delete(ids: List<Int>) = main.deleteItem(ids)
}