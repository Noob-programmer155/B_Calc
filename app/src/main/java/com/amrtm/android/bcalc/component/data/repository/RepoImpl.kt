package com.amrtm.android.bcalc.component.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
//import com.amrtm.android.bcalc.component.data.repository.pagingsource.PagingItemSource
//import com.amrtm.android.bcalc.component.data.repository.pagingsource.PagingItemSourceFilter
//import com.amrtm.android.bcalc.component.data.repository.pagingsource.PagingNoteSource
import com.amrtm.android.bcalc.component.view.convertToItem
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

class BalanceRepo(private val main: StorageBalance): Repository {
    fun get(): Flow<Balance> = main.getBalance()
    override suspend fun add(data: Any) {}
    override suspend fun update(data: Any) = main.updateBalance(data as Balance)
    override suspend fun delete(id: Any) {}
}

class NoteRepo(private val main: StorageNote): Repository {
    fun getAll(name: String, pageSize: Int, dateStart: Date, dateEnd: Date): Flow<PagingData<Note>> = Pager(
        PagingConfig(pageSize = pageSize)
    ) {
        main.getAllNotes(name,dateStart,dateEnd)
//        PagingNoteSource(main,name,dateStart,dateEnd)
    }.flow
    fun get(id: Long?): Flow<NoteItems> = main.getAllItemNotes(id ?: 0)
    override suspend fun add(data: Any) = main.insertNote(data as Note)
    override suspend fun update(data: Any) = main.updateNote(data as Note)
    override suspend fun delete(id: Any) = main.deleteNote(id as IdRef)
}

@Suppress("UNCHECKED_CAST")
class ItemRepo(private val main: StorageItem): Repository {
    fun getAllItems(name: String, pageSize: Int, start: BigDecimal, end: BigDecimal): Flow<PagingData<Item>> = Pager(
        PagingConfig(pageSize = pageSize)
    ) {
        main.getItems(name,start,end)
//        PagingItemSourceFilter(main,name,start,end)
    }.flow
    suspend fun getStock(name: String): Int? = main.getStock(name)
    suspend fun getCount(): Int = main.getCountItems()
    fun get(name: String): Flow<List<ItemHistory>> = main.getHistoryItem(name)
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun addAll(dataItem: List<ItemHistory>) {
        val available = GlobalScope.async { main.checkAvailability(dataItem.map { it.name }) }
        val data = available.await()
        val newItems = convertToItem(dataItem.filter { !data.contains(it.name) }).map { it.copy(id = null) }
        Log.i("new items",newItems.joinToString { it.name })
        val items = convertToItem(dataItem.filter { data.contains(it.name) })
        Log.i("items",items.joinToString { it.name })
        if (newItems.isNotEmpty())
            main.insertItem(newItems)
        if (items.isNotEmpty())
            for (it in items) {
                val job = GlobalScope.launch {
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
                }
                job.join()
            }
        main.insertItemHistory(dataItem)
    }
    override suspend fun add(data: Any) {}
    override suspend fun update(data: Any) {}
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun deleteAll(data: List<ItemHistory>) {
        val job = GlobalScope.launch { main.deleteItemHistory(data.map { IdRef(it.id) })}
        job.join()
        val available = GlobalScope.async {main.checkAvailabilityItemHistory(data.map { it.name })}
        val datas = available.await().distinctBy { it.name }
        Log.i("data delete",datas.joinToString { "{${it.id},${it.name}}" })
        val delete = data.filter {item ->
            !datas.map { it.name }.contains(item.name)
        }.map { NameRef(it.name) }
        Log.i("delete",delete.joinToString { it.name })
        if (delete.isNotEmpty())
            main.deleteItem(delete)
        if (datas.isNotEmpty())
            for (it in datas) {
                val job1 = GlobalScope.launch {
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
                }
                job1.join()
            }
    }
    override suspend fun delete(id: Any) {}
}