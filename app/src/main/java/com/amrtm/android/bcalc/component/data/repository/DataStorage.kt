package com.amrtm.android.bcalc.component.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.room.*
import com.amrtm.android.bcalc.component.data.DataConverter
import com.amrtm.android.bcalc.component.view.convertToItem
import com.amrtm.android.bcalc.component.view.getStatusBalance
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.*
import kotlin.random.Random

// maybe its dumb ways to do test,
// but it can might be useful when you want to test display and data in same time with local database
object Testing {
    val seedCount = 14
    val itemCount = 4
    val randLong = Random.nextLong(1000000,5000000)
    val randomCost = Random.nextLong(1000,100000)
    val randomDiscount = Random.nextInt(0,100)
    val randomPurchase = Random.nextInt(40,400)
    val randomStock = Random.nextInt(0,400)
    val day = 86400000
    val notes = listOf(
        *(0..seedCount).map {
            Note(null,Date(Date().time - (it * day)),"Note ${it+1}",
                "Note ${it+1} Example", BigDecimal.valueOf(randLong),
                BigDecimal.valueOf(randLong),BigDecimal.valueOf(randLong)
            )
        }.toTypedArray()
    )
    val items = listOf(
        *(0..itemCount).map {
            val cost = randomCost
            val discount = randomDiscount
            val purchase = randomPurchase
            val stock = randomStock
            Item(null,
                "ITEM ${it+1}",
                BigDecimal.valueOf(cost),
                BigDecimal.valueOf(cost + 1000),
                discount,
                purchase - 38,
                purchase,
                stock,
                stock + purchase - 38,
                BigDecimal.valueOf((cost+1000) * discount / 100 * (purchase - 38)),
                Date(Date().time - (day * it)))
        }.toTypedArray()
    )

    fun itemHistorys(idsNote: List<Long>) = listOf(
        *(0..seedCount * itemCount).map {
            val count = it % (itemCount+1)
            val note = idsNote[it % (idsNote.size)]
            val cost = randomCost
            val discount = randomDiscount
            val purchase = randomPurchase
            val stock = randomStock
            ItemHistory(null,
                note,
                "ITEM ${count+1}",
                BigDecimal.valueOf(cost),
                BigDecimal.valueOf(cost + 1000),
                discount,
                purchase - 38,
                purchase,
                stock,
                stock + purchase - 38,
                BigDecimal.valueOf((cost+1000) * discount / 100 * (purchase - 38)),
                Date(Date().time - (day * count)))
        }.toTypedArray()
    )
}

@Database(entities = [Balance::class, Note::class, Item::class, ItemHistory::class], version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class BcalcDatabase: RoomDatabase() {
    abstract fun daoBalance(): StorageBalance
    abstract fun daoNote(): StorageNote
    abstract fun daoItem(): StorageItem
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var Instance: BcalcDatabase? = null

        fun getDatabase(context: Context): BcalcDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context,BcalcDatabase::class.java,"BCalc")
                    .fallbackToDestructiveMigration()
                    .build().also { Instance = it }
                // IMPORTANT, dont use this in production
//                    .also {
//                        runBlocking {
//                            val balance = Balance(id = null, income = BigDecimal.ZERO, outcome = BigDecimal.ZERO, profit = BigDecimal.ZERO,
//                                lastUpdate = Date(), status = StatusBalance.Balance, noteCount = 0, itemsCount = 0)
//                            var balanceId = 0L
//                            val balancer = launch { balanceId = it.daoBalance().insertBalance(balance) }
//                            balancer.join()
//                            val arrId = mutableListOf<Long>()
//                            val job1 = launch {
//                                for (nt in Testing.notes)
//                                    arrId.add(it.daoNote().insertNote(nt))
//                            }
//                            job1.join()
//
//                            val itHis = Testing.itemHistorys(arrId)
//                            it.daoItem().insertItemHistory(itHis)
//
//                            //Add Item,Update Note and Balance
//                            val itemSortedMap = itHis.sortedByDescending { it.date }.groupBy { it.name }
//                            val items = itemSortedMap.keys.map { itemSortedMap[it]?.get(0)!! }
//                            it.daoItem().insertItem(convertToItem(items.sortedByDescending { it.date }.distinctBy { it.name }))
//
//                            val itemSorted = itHis.groupBy { it.note }
//                            arrId.mapIndexed {index,i ->
//                                it.daoNote().updateNote(Testing.notes[index].copy(
//                                    id = i,
//                                    income = itemSorted[i]!!.sumOf {item -> item.total.minus(BigDecimal.valueOf(item.buyCost.toLong() * item.purchased)) },
//                                    outcome = itemSorted[i]!!.sumOf { item -> BigDecimal.valueOf(item.buyCost.toLong() * item.purchased) }
//                                ))
//                            }
//
//                            val income = itHis.sumOf { item -> item.total }
//                            val outcome = itHis.sumOf { item -> BigDecimal.valueOf(item.buyCost.toLong() * item.purchased) }
//                            val profit = getStatusBalance(income, outcome)
//                            it.daoBalance().updateBalance(balance.copy(
//                                id = balanceId,
//                                income = income,
//                                outcome = outcome,
//                                profit = profit.second,
//                                status = profit.first
//                            ))
//                        }
//                    }
            }
        }
    }
}

const val balanceName: String = "balance"

@Dao
@TypeConverters(DataConverter::class)
interface StorageBalance {
    @Query("SELECT * FROM balance WHERE name = '${balanceName}'")
    fun getBalance(): Flow<Balance>

    // create at init database
    @Insert
    suspend fun insertBalance(balance: Balance): Long

    @Update
    suspend fun updateBalance(balance: Balance)
}

@Dao
@TypeConverters(DataConverter::class)
interface StorageNote {
    @Query("SELECT * FROM note WHERE date BETWEEN :start AND :end AND name LIKE '%'|| :name ||'%' ORDER BY name,id")
    fun getAllNotes(name: String, start: Date, end: Date): PagingSource<Int,Note>

    @Transaction
    @Query("SELECT * FROM note WHERE id = :id")
    fun getAllItemNotes(id: Long): Flow<NoteItems>

    @Insert
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete(entity = Note::class)
    suspend fun deleteNote(id: IdRef)
}

data class NoteItems(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "id",
        entityColumn = "note")
    val items: List<ItemHistory>
)

@Dao
@TypeConverters(DataConverter::class)
interface StorageItem {
    @Query("SELECT * FROM item WHERE sell_cost BETWEEN :start AND :end AND name LIKE '%'|| :name ||'%' ORDER BY name,id")
    fun getItems(name: String,start: BigDecimal, end: BigDecimal): PagingSource<Int,Item>

    @Query("SELECT * FROM item ORDER BY name,id")
    fun getItems(): PagingSource<Int,Item>

    @Query("SELECT COUNT(*) FROM item")
    suspend fun getCountItems(): Int

    @Query("SELECT stock FROM itemHistory WHERE name = :name ORDER BY date DESC LIMIT 1")
    suspend fun getStock(name: String): Int?

    @Query("SELECT name FROM item WHERE name IN(:names)")
    suspend fun checkAvailability(names: List<String>): List<String>

    @Query("SELECT * FROM itemHistory WHERE name IN(:names) ORDER BY date,id DESC")
    suspend fun checkAvailabilityItemHistory(names: List<String>): List<ItemHistory>

    //Data Visualization
    @Query("SELECT * FROM itemHistory WHERE name = :name ORDER BY date,name ASC")
    fun getHistoryItem(name: String): Flow<List<ItemHistory>>

//    @Query("SELECT * from item WHERE id = :id")
//    fun getItem(id: Int): Flow<Item>

//    @Insert(Item::class)
//    suspend fun insertItem(item: Item): Int

    @Insert(Item::class)
    suspend fun insertItem(item: List<Item>): List<Long>

    @Insert(ItemHistory::class)
    suspend fun insertItemHistory(item: List<ItemHistory>): List<Long>

    @Query("UPDATE item SET " +
            "buy_cost = :buyCost, sell_cost = :sellCost, discount = :discount, sold_out = :soldOut," +
            "purchased = :purchased, stock = :stock, total_item = :totalItem, total = :total, date = :date" +
            " WHERE name = :name")
    suspend fun updateItem(
        buyCost: BigDecimal,
        sellCost: BigDecimal,
        discount: Int,
        soldOut: Int,
        purchased: Int,
        stock: Int,
        totalItem: Int,
        total: BigDecimal,
        date: Date,
        name: String
    ): Int
//
//    @Update(Item::class)
//    suspend fun updateItem(item: List<Item>): List<Int>

//    @Query("DELETE FROM item WHERE id = :id")
//    suspend fun deleteItem(id: Int)

    @Delete(entity = Item::class)
    suspend fun deleteItem(ids: List<NameRef>)

    @Delete(entity = ItemHistory::class)
    suspend fun deleteItemHistory(id: List<IdRef>)
}
