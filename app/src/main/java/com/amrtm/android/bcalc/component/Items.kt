package com.amrtm.android.bcalc.component

import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp
import com.amrtm.android.bcalc.component.data.DataStorage
import com.amrtm.android.bcalc.component.data.ItemDetail
import java.math.BigDecimal

@Composable
fun ItemsList(
    storage: DataStorage,
    sizeItem: Int,
    statePage: MutableState<Int> = remember { mutableStateOf(0) },
    stateSearchItem: MutableState<String> = remember { mutableStateOf("") },
    stateFilterItemsStart: MutableState<BigDecimal> = remember { mutableStateOf(BigDecimal.ZERO) },
    stateFilterItemsEnd: MutableState<BigDecimal> = remember { mutableStateOf(BigDecimal.ZERO) },
    stateCollapse: MutableState<Int>,
) {
    val dataItem = storage.listItem.filter { it.item.name.contains(stateSearchItem.value) }.sortedBy { it.item.name }.
    subList(statePage.value*sizeItem,(statePage.value+1)*sizeItem)
    val listItem = remember { mutableStateListOf<ItemDetail>(*dataItem.toTypedArray()) }

}