package com.amrtm.android.bcalc.component.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.*

enum class StatusBalance(val id: Int, val icon:ImageVector?, val color: Color) {
    Profit(0, Icons.Filled.ArrowDropUp, Color.Green),
    Balance(1, null, Color.Yellow),
    Loss(2, Icons.Filled.ArrowDropDown, Color.Red)
}

@Entity
data class Balance(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("income") val income: BigDecimal?,
    @ColumnInfo("outcome") val outcome: BigDecimal?,
    @ColumnInfo("profit") val profit: BigDecimal?,
    @ColumnInfo("lastUpdate") val lastUpdate: Date?,
    @ColumnInfo("status") val status : StatusBalance?,
    @ColumnInfo("noteCount") val noteCount: Int?,
    @ColumnInfo("itemsCount") val itemsCount: Int?
)

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo("date") val date: Date,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("income") val income: BigDecimal,
    @ColumnInfo("outcome") val outcome: BigDecimal,
)

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo("note") val note: Int,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("cost") val cost: BigDecimal,
    @ColumnInfo("discount") val discount: Int,
    @ColumnInfo("sold_out") val sold_out: Int,
    @ColumnInfo("stock") val stock: Int,
    @ColumnInfo("total_item") val total_item: Int,
    @ColumnInfo("total") val total: BigDecimal,
    @ColumnInfo("date") val date: Date
)

data class ItemRaw(
    val id: Int? = null,
    val name: String,
    val cost: BigDecimal,
    val discount: Int,
    val sold_out: Int,
    val stock: Int,
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