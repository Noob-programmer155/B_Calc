package com.amrtm.android.bcalc.component.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.*

enum class StatusBalance(val id: Int, val icon:ImageVector?, val color: Color) {
    Profit(0, Icons.Filled.ArrowDropUp, Color.Green),
    Balance(1, null, Color.Yellow),
    Loss(2, Icons.Filled.ArrowDropDown, Color.Red)
}

data class IdRef(
    val id: Long?
)

data class NameRef(
    val name: String
)

data class IdAndCount(
    val id: Long?,
    val count: Int
)

@Entity
data class Balance(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo("name") val name: String = balanceName,
    @ColumnInfo("income") val income: BigDecimal?,
    @ColumnInfo("outcome") val outcome: BigDecimal?,
    @ColumnInfo("profit") val profit: BigDecimal?,
    @ColumnInfo("lastUpdate") val lastUpdate: Date?,
    @ColumnInfo("status") val status : StatusBalance?,
    @ColumnInfo("noteCount") val noteCount: Int?,
    @ColumnInfo("itemsCount") val itemsCount: Int?
)

@Entity(indices = [Index(value = ["id"], unique = true)])
data class Note(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("id") val id: Long?,
    @ColumnInfo("date") val date: Date,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("income") val income: BigDecimal,
    @ColumnInfo("outcome") val outcome: BigDecimal,
    @ColumnInfo("additional outcome") val additionalOutcome: BigDecimal
)

@Entity(indices = [Index(value = ["id","name"], unique = true)])
data class Item(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("id") val id: Long?,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("buy_cost") val buyCost: BigDecimal,
    @ColumnInfo("sell_cost") val sellCost: BigDecimal,
    @ColumnInfo("discount") val discount: Int,
    @ColumnInfo("sold_out") val sold_out: Int,
    @ColumnInfo("purchased") val purchased: Int,
    @ColumnInfo("stock") val stock: Int = 0,
    @ColumnInfo("total_item") val total_item: Int,
    @ColumnInfo("total") val total: BigDecimal,
    @ColumnInfo("date") val date: Date
)

// ===============================================================================================
// best implementation using relation again,
// but because in item all of column is dynamic except name and id,
// so using new entity instead relation
// ==============================================================================================
//@Entity(tableName = "itemHistory", indices = [Index(value = ["id"], unique = true)])
//data class ItemHistory(
//    @PrimaryKey(autoGenerate = true) @ColumnInfo("id") val id: Long?,
//    @ColumnInfo("note") val note: Long,
//    @ColumnInfo("item") val name: Long,
//    @ColumnInfo("buy_cost") val buyCost: BigDecimal,
//    @ColumnInfo("sell_cost") val sellCost: BigDecimal,
//    @ColumnInfo("discount") val discount: Int,
//    @ColumnInfo("sold_out") val sold_out: Int,
//    @ColumnInfo("purchased") val purchased: Int,
//    @ColumnInfo("stock") val stock: Int = 0,
//    @ColumnInfo("total_item") val total_item: Int,
//    @ColumnInfo("total") val total: BigDecimal,
//    @ColumnInfo("date") val date: Date
//)

@Entity(tableName = "itemHistory", indices = [Index(value = ["id"], unique = true)])
data class ItemHistory(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("id") val id: Long?,
    @ColumnInfo("note") val note: Long,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("buy_cost") val buyCost: BigDecimal,
    @ColumnInfo("sell_cost") val sellCost: BigDecimal,
    @ColumnInfo("discount") val discount: Int,
    @ColumnInfo("sold_out") val sold_out: Int,
    @ColumnInfo("purchased") val purchased: Int,
    @ColumnInfo("stock") val stock: Int = 0,
    @ColumnInfo("total_item") val total_item: Int,
    @ColumnInfo("total") val total: BigDecimal,
    @ColumnInfo("date") val date: Date
)

data class ItemRaw(
    val id: Long? = null,
    val name: String,
    val buyCost: BigDecimal,
    val sellCost: BigDecimal,
    val discount: Int,
    val sold_out: Int,
    val purchased: Int,
    val stock: Int?,
    val total: BigDecimal,
    val date: Date = Date(),
    val delete: Boolean = false
)

//
//data class ItemDetail(
//    val id: Int,
//    val item: Item,
//    val meanPrice: BigDecimal,
//    val lowestPrice: BigDecimal,
//    val higherPrice: BigDecimal
//)