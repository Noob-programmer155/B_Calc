package com.amrtm.android.bcalc.component.view

import android.content.Context
import android.widget.Button
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.FilterList
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.amrtm.android.bcalc.component.data.repository.Item
import com.amrtm.android.bcalc.component.data.repository.ItemRaw
import com.amrtm.android.bcalc.component.data.viewmodel.ItemViewModel
import com.amrtm.android.bcalc.component.data.viewmodel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

//fun dataManipulation(notes: MutableSet<Note>, itemSize: Int): Balance {
//    val incomes = notes.fold(BigDecimal.ZERO) {s,o -> s.plus(o.income!!)}
//    val outcomes = notes.fold(BigDecimal.ZERO) {s,o -> s.plus(o.outcome!!)}
//    return Balance(
//        incomes!!,
//        outcomes,
//        incomes.minus(outcomes),
//        notes.maxOf { it.date },
//        if(incomes.minus(outcomes) > BigDecimal.ZERO) StatusBalance.Profit
//        else
//            if (incomes.minus(outcomes) == BigDecimal.ZERO) StatusBalance.Balance
//            else StatusBalance.Loss,
//        notes.size,
//        itemSize
//    )
//}
//
//fun dataItemManipulation(items: MutableSet<Item>): List<ItemDetail> {
//    val itemMan = items.map {it ->
//        ItemDetail(
//            it.id,
//            it,
//            (it.cost_history.fold(BigDecimal.ZERO) { s, o -> s.plus(o)}).divide(BigDecimal(it.cost_history.size)),
//            it.cost_history.minOf { it },
//            it.cost_history.maxOf { it }
//        )
//    }
//    return itemMan
//}

fun convertToItem(itemRaws: List<ItemRaw>, noteId: Int): List<Item> {
    return itemRaws.map {
        Item(
            id = it.id,
            note = noteId,
            name = it.name.uppercase(),
            cost = it.cost,
            discount = it.discount,
            sold_out = it.sold_out,
            stock = it.stock,
            total_item = it.sold_out + it.stock,
            total = it.total,
            date = Date()
        )
    }
}

fun getIdFromList(items: List<Any>): List<Int> {
    return items.map {
        when(it) {
            is Item -> it.id!!
            is ItemRaw -> it.id!!
            else -> -1
        }
    }
}

fun convertToItemRaw(items: List<Item>): List<ItemRaw> {
    return items.map {
        ItemRaw(
            id = it.id,
            name = it.name.uppercase(),
            cost = it.cost,
            discount = it.discount,
            sold_out = it.sold_out,
            stock = it.stock,
            total = it.total,
            date = it.date
        )
    }
}

@Composable
fun Filter(
    view: NoteViewModel,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    thread: CoroutineScope = rememberCoroutineScope(),
    context: Context
) {
    val dateStartChange: MutableState<Date> = remember { mutableStateOf(Date()) }
    val dateEndChange: MutableState<Date> = remember { mutableStateOf(Date()) }
    ModalDrawer(
        drawerContent = {
            Text(text = "Filter: ", style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
            Text(text = "from: ", style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
            Text(text = "change date value", style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)
            DatePicker(context).init(
                SimpleDateFormat("yyyy", Locale.US).format(dateStartChange).toInt(),
                SimpleDateFormat("MM", Locale.US).format(dateStartChange).toInt(),
                SimpleDateFormat("dd", Locale.US).format(dateStartChange).toInt()
            ) { _, y, m, d ->
                dateStartChange.value = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse("${y}-${m}-${d}")!!
            }
            Text(text = " to ", style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
            Text(text = "change date value", style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)
            DatePicker(context).init(
                SimpleDateFormat("yyyy", Locale.US).format(dateEndChange).toInt(),
                SimpleDateFormat("MM", Locale.US).format(dateEndChange).toInt(),
                SimpleDateFormat("dd", Locale.US).format(dateEndChange).toInt()
            ) { _, y, m, d ->
                dateEndChange.value = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse("${y}-${m}-${d}")!!
            }
            Button(modifier = Modifier
                .padding(30.dp, 0.dp, 0.dp, 0.dp)
                .padding(10.dp),
                onClick = {
                    thread.launch {
                        drawerState.close()
                    }
                    view.onFilter(dateStartChange.value, dateEndChange.value)
                }
            ) {
                Text(text = "Done")
            }
        }
    ) {
        IconButton(onClick = { thread.launch { drawerState.open() } }) {
            Icon(imageVector = Icons.Sharp.FilterList, contentDescription = "filter")
        }
    }
}

@Composable
fun Filter(
    view: ItemViewModel,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    thread: CoroutineScope = rememberCoroutineScope(),
) {
    val costStartChange: MutableState<BigDecimal> = remember { mutableStateOf(BigDecimal.ZERO) }
    val costEndChange: MutableState<BigDecimal> = remember { mutableStateOf(BigDecimal.ZERO) }
    ModalDrawer(
        drawerContent = {
            Text(text = "Range Cost: ", style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
            OutlinedTextField(
                value = DecimalFormat("#,###.00").format(costStartChange.value),
                onValueChange = { costStartChange.value = DecimalFormat("#,###.00").parse(it) as BigDecimal },
                label = {Text(text = "start cost value", style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)},
                leadingIcon = { Text(text = "Rp. ",style = MaterialTheme.typography.h6) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            )
            OutlinedTextField(
                value = DecimalFormat("#,###.00").format(costEndChange.value),
                onValueChange = { costEndChange.value = DecimalFormat("#,###.00").parse(it) as BigDecimal },
                leadingIcon = { Text(text = "Rp. ",style = MaterialTheme.typography.h6) },
                label = {Text(text = "end cost value", style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            )
            Button(
                modifier = Modifier
                    .padding(30.dp, 0.dp, 0.dp, 0.dp)
                    .padding(10.dp),
                onClick = {
                    thread.launch {
                        drawerState.close()
                    }
                    view.onFilter(costStartChange.value,costEndChange.value)
                }
            ) {
                Text(text = "Done")
            }
        }
    ) {
        IconButton(onClick = { thread.launch { drawerState.open() } }) {
            Icon(imageVector = Icons.Sharp.FilterList, contentDescription = "filter")
        }
    }
}

@Composable
fun MessageDialog(
    title: String,
    body: @Composable () -> Unit,
    doneText: String = "Done",
    onDone: () -> Unit,
    closeText: String = "Close",
    withCloseButton: Boolean = true,
    open: MutableState<Boolean>,
    onDismiss: () -> Unit = {}
) {
    if (open.value) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = title, style = MaterialTheme.typography.h3)
            },
            text = body,
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(modifier = Modifier.padding(15.dp,0.dp).padding(10.dp), onClick = onDone) {
                        Text(text = doneText)
                    }
                    if (withCloseButton) {
                        TextButton(modifier = Modifier.padding(10.dp), onClick = { open.value = !open.value }) {
                            Text(text = closeText)
                        }
                    }
                }
            }
        )
    }
}

//@Composable
//fun Pagination (
//    disableNextState: MutableState<Boolean> = remember { mutableStateOf(false) },
//    disablePrevState: MutableState<Boolean> = remember { mutableStateOf(false) },
//    statePage: MutableState<Int>,
//    dataOut: SnapshotStateList<Any>,
//    dataIn: List<Any>
//) {
//    Row (verticalAlignment = Alignment.CenterVertically) {
//        PaginationButton(
//            disableState = disablePrevState,
//            statePage = statePage,
//            dataOut = dataOut,
//            dataIn = dataIn,
//            isNextButton = false
//        )
//        Spacer(modifier = Modifier
//            .fillMaxWidth()
//            .weight(1f))
//        PaginationButton(
//            disableState = disableNextState,
//            statePage = statePage,
//            dataOut = dataOut,
//            dataIn = dataIn,
//            isNextButton = true
//        )
//    }
//}
//
//@Composable
//private fun PaginationButton (
//    disableState: MutableState<Boolean>,
//    statePage: MutableState<Int>,
//    dataOut: SnapshotStateList<Any>,
//    dataIn: List<Any>,
//    isNextButton: Boolean,
//){
//    IconButton(
//        modifier = Modifier
//            .padding(20.dp, 0.dp)
//            .padding(0.dp),
//        enabled = disableState.value,
//        onClick = {
//            if (isNextButton) {
//                if(statePage.value+1 <= dataOut.size) {
//                    if (statePage.value+2 > dataOut.size) {
//                        disableState.value = !disableState.value
//                    }
//                    statePage.value += 1
//                    dataOut.removeRange(0,dataOut.size)
//                    dataOut.addAll(dataIn)
//                }
//            } else {
//                if(statePage.value-1 >= 1) {
//                    if (statePage.value-2 < 1) {
//                        disableState.value = !disableState.value
//                    }
//                    statePage.value -= 1
//                    dataOut.removeRange(0,dataOut.size)
//                    dataOut.addAll(dataIn)
//                }
//            }
//        }
//    ) {
//        Icon(imageVector = Icons.Outlined.FirstPage, contentDescription = "previous")
//    }
//}
//
fun colorGenerate(): SnapshotStateMap<Char,Color> {
    val colors: SnapshotStateMap<Char, Color> = mutableStateMapOf()
    val red = setOf(0xFFFF0032,0xFFFF8B13,0xFFFF7B54,0xFFFFB26B,0xFFFFD56F,0xFFFFD495,0xFFFF6E31,0xFFF1F7B5,0xFFFEC868)
    val green = setOf(0xFFABC270,0xFF03C988,0xFF4E6C50,0xFF939B62,0xFF61876E,0xFF3C6255,0xFFA6BB8D,0xFF227C70)
    val blue = setOf(0xFF00337C,0xFF1C82AD,0xFF0081B4,0xFF8DCBE6,0xFFA0C3D2,0xFF6C00FF,0xFF5BC0F8,0xFF86C8BC)
    for (i in 1..25) {
        if (i <= 9)
            colors[(64+i).toChar()] = Color(red.elementAt(i-1))
        else if (i <= 17)
            colors[(64+i).toChar()] = Color(green.elementAt(i-9-1))
        else
            colors[(64+i).toChar()] = Color(blue.elementAt(i-17-1))
    }
    return colors
}