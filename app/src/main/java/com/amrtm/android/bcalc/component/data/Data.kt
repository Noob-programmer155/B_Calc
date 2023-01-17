package com.amrtm.android.bcalc.component.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.math.BigDecimal
import java.util.Date

enum class StatusBalance(val id: Int, val icon:ImageVector?, val color: Color) {
    Profit(0, Icons.Filled.ArrowDropUp, Color.Green),
    Balance(1, null, Color.Yellow),
    Loss(2, Icons.Filled.ArrowDropDown, Color.Red)
}

data class Balance(
    var income: BigDecimal = BigDecimal.ZERO,
    var outcome: BigDecimal = BigDecimal.ZERO,
    var profit: BigDecimal = BigDecimal.ZERO,
    var lastUpdate: Date = Date(),
    var status : StatusBalance = StatusBalance.Balance,
    var noteCount: Int = 0,
    var itemsCount: Int = 0
)

data class Note(
    val id: Int,
    val date: Date,
    val name: String,
    val description: String,
    val income: BigDecimal,
    val outcome: BigDecimal,
    val items: List<Int>
)

data class Item(
    val id: Int,
    val name: String,
    val cost_history: List<BigDecimal>,
    val discount_history: List<Int>,
    val quantity_history: List<Int>,
    val not_sold: List<Int>
)

data class ItemDetail(
    val id: Int,
    val item: Item,
    val meanPrice: BigDecimal,
    val lowestPrice: BigDecimal,
    val higherPrice: BigDecimal
)