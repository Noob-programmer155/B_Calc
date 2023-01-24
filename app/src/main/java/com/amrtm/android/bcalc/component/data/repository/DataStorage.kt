package com.amrtm.android.bcalc.component.data.repository

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.Database
import com.amrtm.android.bcalc.component.data.DataConverter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.Date

@Database(entities = [Balance::class, Note::class, Item::class], version = 1, exportSchema = false)
abstract class Database (val context: Context): RoomDatabase() {
    abstract fun daoBalance(): StorageBalance
    abstract fun daoNote(): StorageNote
    abstract fun daoItem(): StorageItem
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var Instance: com.amrtm.android.bcalc.component.data.repository.Database? = null

        fun getDatabase(context: Context): com.amrtm.android.bcalc.component.data.repository.Database {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context,com.amrtm.android.bcalc.component.data.repository.Database::class.java,"BCalc")
                    .addTypeConverter(DataConverter::class.java)
                    .fallbackToDestructiveMigration()
                    .build().also { Instance = it }
            }
        }
    }
}

@Dao
interface StorageBalance {
    @Query("SELECT * from balance WHERE name = :name")
    fun getBalance(name: String = "myBalance"): Flow<Balance>

    @Insert(Balance::class)
    suspend fun insertBalance(balance: Balance): Int

    @Update(Balance::class)
    suspend fun updateBalance(balance: Balance): Int
}

@Dao
interface StorageNote {
    @Query("SELECT * from note WHERE date BETWEEN :start AND :end AND name LIKE '%'|| :name ||'%'")
    fun getAllNotes(name: String, start: Date, end: Date): PagingSource<Int, Note>

    @Query("SELECT * from note JOIN item ON note.id = item.note WHERE note.id = :id")
    fun getAllItemNotes(id: Int): Flow<Map<Note,List<Item>>>

    @Insert(Note::class)
    suspend fun insertNote(note: Note): Int

    @Update(Note::class)
    suspend fun updateNote(note: Note): Int

    @Query("DELETE FROM note WHERE id = :id")
    suspend fun deleteNote(id: Int)
}

@Dao
interface StorageItem {
    @Query("SELECT * from item AS t1 LEFT JOIN item AS t2 " +
            "ON t1.name = t2.name AND t1.date < t2.date OR (t1.name = t2.name AND t1.date = t2.date AND t1.id < t2.id) " +
            "WHERE t2.id IS NULL AND t1.cost BETWEEN :start AND :end AND t1.name LIKE '%'|| :name ||'%'")
    fun getGroupingItems(name: String,start: BigDecimal, end: BigDecimal): PagingSource<Int, Item>

    //Data Visualization
    @Query("SELECT * from item WHERE name = :name")
    fun getHistoryItem(name: String): Flow<List<Item>>

//    @Query("SELECT * from item WHERE id = :id")
//    fun getItem(id: Int): Flow<Item>

    @Insert(Item::class)
    suspend fun insertItem(item: Item): Int

    @Insert(Item::class)
    suspend fun insertItem(item: List<Item>): List<Int>

    @Update(Item::class)
    suspend fun updateItem(item: Item): Int

    @Update(Item::class)
    suspend fun updateItem(item: List<Item>): List<Int>

    @Query("DELETE FROM item WHERE id = :id")
    suspend fun deleteItem(id: Int)

    @Query("DELETE FROM item WHERE id IN (:ids)")
    suspend fun deleteItem(ids: List<Int>)
}