package com.amrtm.android.bcalc.component.data.repository

import android.annotation.SuppressLint
import android.content.Context
import androidx.paging.PagingSource
import androidx.room.*
import com.amrtm.android.bcalc.component.data.DataConverter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.*
import java.util.jar.Attributes.Name

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
                    .also {
                        runBlocking {
                            it.daoBalance().insertBalance(
                                Balance(id = null, income = BigDecimal.ZERO, outcome = BigDecimal.ZERO, profit = BigDecimal.ZERO,
                                    lastUpdate = Date(), status = StatusBalance.Balance, noteCount = 0, itemsCount = 0)
                            )
                        }
                    }
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
