package com.amrtm.android.bcalc.component.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.amrtm.android.bcalc.component.data.repository.pagingsource.PagingItemSource
import com.amrtm.android.bcalc.component.data.repository.pagingsource.PagingItemSourceFilter
import com.amrtm.android.bcalc.component.data.repository.pagingsource.PagingNoteSource
import com.amrtm.android.bcalc.component.view.convertToItem
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.*

class BalanceRepo(private val main: StorageBalance): Repository {
    fun get(): Flow<Balance> = main.getBalance()
    override suspend fun add(data: Any) {}
    override suspend fun update(data: Any) = main.updateBalance(data as Balance)
    override fun delete(id: Any) {}
}

class NoteRepo(private val main: StorageNote): Repository {
    fun getAll(name: String, pageSize: Int, dateStart: Date, dateEnd: Date): Flow<PagingData<Note>> = Pager(
        PagingConfig(pageSize = pageSize)
    ) {
        PagingNoteSource(main,name,dateStart,dateEnd)
    }.flow
    fun get(id: Long?): Flow<NoteItems> = main.getAllItemNotes(id ?: 0)
    override suspend fun add(data: Any) = main.insertNote(data as Note)
    override suspend fun update(data: Any) = main.updateNote(data as Note)
    override fun delete(id: Any) = main.deleteNote(id as IdRef)
}

@Suppress("UNCHECKED_CAST")
class ItemRepo(private val main: StorageItem): Repository {
    fun getAllItems(name: String, pageSize: Int, start: BigDecimal, end: BigDecimal): Flow<PagingData<Item>> = Pager(
        PagingConfig(pageSize = pageSize)
    ) {
        PagingItemSourceFilter(main,name,start,end)
    }.flow
    fun getAllItems(pageSize: Int): Flow<PagingData<Item>> = Pager(
        PagingConfig(pageSize = pageSize)
    ) {
        PagingItemSource(main)
    }.flow
    fun getStock(name: String): Flow<Int?> = main.getStock(name)
    fun getCount(): Flow<Int> = main.getCountItems()
    fun get(name: String): Flow<List<ItemHistory>> = main.getHistoryItem(name)
    suspend fun addAll(dataItem: List<ItemHistory>) {
        val available = main.checkAvailability(dataItem.map { it.name })
        val newItems = convertToItem(dataItem.filter { !available.contains(it.name) }).map { it.copy(id = null) }
        val items = convertToItem(dataItem.filter { available.contains(it.name) })
        if (newItems.isNotEmpty())
            main.insertItem(newItems)
        else
            for (it in items)
                main.updateItem(
                    it.buyCost,
                    it.sellCost,
                    it.discount,
                    it.sold_out,
                    it.purchased,
                    it.stock,
                    it.total_item,
                    it.total,
                    it.date,
                    it.name
                )
        main.insertItemHistory(dataItem)
    }
    override suspend fun add(data: Any) {}
    override suspend fun update(data: Any) {}
    suspend fun deleteAll(data: List<ItemHistory>) {
        main.deleteItemHistory(data.map { IdRef(it.id) })
        val available = main.checkAvailability(data.map { it.name })
        val delete = data.filter { !available.contains(it.name) }.map { IdRef(it.id) }
        if (delete.isNotEmpty())
            main.deleteItem(delete)
    }
    override fun delete(id: Any) {}
}