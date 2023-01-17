package com.amrtm.android.bcalc.component

import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.FilterList
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.amrtm.android.bcalc.component.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun DataManipulation(notes: MutableSet<Note>, itemSize: Int): Balance {
    val incomes = notes.fold(BigDecimal.ZERO) {s,o -> s.plus(o.income)}
    val outcomes = notes.fold(BigDecimal.ZERO) {s,o -> s.plus(o.outcome)}
    return Balance(
        incomes,
        outcomes,
        incomes.minus(outcomes),
        notes.maxOf { it.date },
        if(incomes.minus(outcomes) > BigDecimal.ZERO) StatusBalance.Profit
        else
            if (incomes.minus(outcomes) == BigDecimal.ZERO) StatusBalance.Balance
            else StatusBalance.Loss,
        notes.size,
        itemSize
    )
}

fun DataItemManipulation(items: MutableSet<Item>): List<ItemDetail> {
    val itemMan = items.map {it ->
        ItemDetail(
            it.id,
            it,
            (it.cost_history.fold(BigDecimal.ZERO) { s, o -> s.plus(o)}).divide(BigDecimal(it.cost_history.size)),
            it.cost_history.minOf { it },
            it.cost_history.maxOf { it }
        )
    }
    return itemMan
}

@Composable
fun Filter(
    dateStart: MutableState<Date>,
    dateEnd: MutableState<Date>,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    thread: CoroutineScope = rememberCoroutineScope(),
    dataIn: List<Note>,
    dataOut: SnapshotStateList<Note>,
    pagination: MutableState<Int>,
    context: Context
) {
    ModalDrawer(
        drawerContent = {
            Text(text = "Filter: ", style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
            Text(text = "from: ", style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
            Text(text = "change date value", style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)
            DatePicker(context).init(
                SimpleDateFormat("yyyy", Locale.US).format(dateStart).toInt(),
                SimpleDateFormat("MM", Locale.US).format(dateStart).toInt(),
                SimpleDateFormat("dd", Locale.US).format(dateStart).toInt(),
                {
                        view, y, m, d -> dateStart.value = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse("${y}-${m}-${d}")!!
                }
            )
            Text(text = " to ", style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
            Text(text = "change date value", style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)
            DatePicker(context).init(
                SimpleDateFormat("yyyy", Locale.US).format(dateEnd).toInt(),
                SimpleDateFormat("MM", Locale.US).format(dateEnd).toInt(),
                SimpleDateFormat("dd", Locale.US).format(dateEnd).toInt(),
                {
                        view, y, m, d -> dateEnd.value = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse("${y}-${m}-${d}")!!
                }
            )
            Button(modifier = Modifier.padding(30.dp,0.dp,0.dp,0.dp).padding(10.dp), onClick = { thread.launch { drawerState.close() } }) {
                Text(text = "Done")
            }
        }
    ) {
        IconButton(onClick = {
            thread.launch {
                drawerState.open()
                dataOut.removeRange(0,dataOut.size)
                dataOut.addAll(dataIn)
                pagination.value = dataOut.size
            }
        }) {
            Icon(imageVector = Icons.Sharp.FilterList, contentDescription = "filter")
        }
    }
}

@Composable
fun Filter(
    costStart: MutableState<BigDecimal>,
    costEnd: MutableState<BigDecimal>,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    thread: CoroutineScope = rememberCoroutineScope()
) {
    ModalDrawer(
        drawerContent = {
            Text(text = "Range Cost: ", style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
            Text(text = "start cost value", style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)
            OutlinedTextField(
                value = DecimalFormat("#,###.00").format(costStart.value),
                leadingIcon = { Text(text = "Rp. ",style = MaterialTheme.typography.h6) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                onValueChange = { costStart.value = DecimalFormat("#,###.00").parse(it) as BigDecimal }
            )
            Text(text = "end cost value", style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)
            OutlinedTextField(
                leadingIcon = { Text(text = "Rp. ",style = MaterialTheme.typography.h6) },
                value = DecimalFormat("#,###.00").format(costEnd.value),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                onValueChange = { costEnd.value = DecimalFormat("#,###.00").parse(it) as BigDecimal }
            )
            Button(modifier = Modifier.padding(30.dp,0.dp,0.dp,0.dp).padding(10.dp), onClick = { thread.launch { drawerState.close() } }) {
                Text(text = "Done")
            }
        }
    ) {
        IconButton(onClick = { thread.launch { drawerState.open() } }) {
            Icon(imageVector = Icons.Sharp.FilterList, contentDescription = "filter")
        }
    }
}