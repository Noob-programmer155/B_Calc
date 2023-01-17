package com.amrtm.android.bcalc.component

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.amrtm.android.bcalc.component.data.*
import com.amrtm.android.bcalc.ui.theme.defaultNoteBorderColor
import com.amrtm.android.bcalc.ui.theme.defaultNoteBorderColorDark
import com.amrtm.android.bcalc.ui.theme.defaultNoteColor
import com.amrtm.android.bcalc.ui.theme.defaultNoteColorDark
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardAddLayout(
    navController: NavHostController,
    height: Dp,
    storage: DataStorage,
    sizeItem: Int,
    statePage: MutableState<Int> = remember { mutableStateOf(0) },
    stateSearchNote: MutableState<String> = remember { mutableStateOf("") },
    stateFilterNoteStart: MutableState<Date> = remember { mutableStateOf(Date()) },
    stateFilterNoteEnd: MutableState<Date> = remember { mutableStateOf(Date()) },
    stateCollapse: MutableState<Int>,
    context: Context
) {
    val dataList = storage.listNote.filter { it.name.contains(stateSearchNote.value) }.sortedBy { it.date }.
        subList(statePage.value*sizeItem,(statePage.value+1)*sizeItem)
    val listNote = remember { mutableStateListOf<Note>(*dataList.toTypedArray()) }
    val maxPages = remember { mutableStateOf<Int>(dataList.size) }
    Card(
        onClick = {navController.navigate(route = Navigation.Note.link)},
        modifier = Modifier
            .padding(0.dp, 20.dp, 0.dp, 0.dp)
            .fillMaxWidth()
            .padding(0.dp),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = if (isSystemInDarkTheme()) defaultNoteColor.copy(alpha = .6f) else defaultNoteColorDark.copy(alpha = .6f),
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 20.dp,
        border = BorderStroke(10.dp, if (isSystemInDarkTheme()) defaultNoteBorderColor.copy(alpha = .6f) else defaultNoteBorderColorDark.copy(alpha = .6f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            contentAlignment = Alignment.Center
        ) {
            Icon(modifier = Modifier.fillMaxSize(.2f),
                imageVector = Icons.Filled.AddCircle,
                contentDescription = null,
                tint = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
            )
        }
    }
    Row {
        OutlinedTextField(
            value = stateSearchNote.value,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {}),
            onValueChange = { stateSearchNote.value = it })
        Filter(
            dateStart = stateFilterNoteStart,
            dateEnd = stateFilterNoteEnd,
            context = context,
            dataIn = dataList,
            dataOut = listNote,
            pagination = maxPages
        )
    }
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        items(listNote){
            Card(
                onClick = {
                    if (stateCollapse.value == it.id)
                        stateCollapse.value = -2
                    else
                        stateCollapse.value = it.id
                          },
                enabled = true,
                modifier = Modifier
                    .padding(0.dp, 25.dp, 0.dp, 0.dp)
                    .fillMaxWidth()
                    .padding(15.dp),
                shape = MaterialTheme.shapes.medium,
                backgroundColor = if (isSystemInDarkTheme()) defaultNoteColor else defaultNoteColorDark,
                contentColor = MaterialTheme.colors.onPrimary,
                elevation = 35.dp,
                border = BorderStroke(10.dp, if (isSystemInDarkTheme()) defaultNoteBorderColor else defaultNoteBorderColorDark)
            ) {
                Column {
                    Row (verticalAlignment = Alignment.Top) {
                        Text(text = it.name, style = MaterialTheme.typography.h3, maxLines = 2,
                            softWrap = true, overflow = TextOverflow.Ellipsis)
                        Text(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), textAlign = TextAlign.End,
                            text = SimpleDateFormat("dd/MMM/yyyy", Locale.US).format(it.date),
                            style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
                    }
                    Text(text = "Income: ${DecimalFormat("#,###.00").format(it.income)}", style = MaterialTheme.typography.h6)
                    Text(text = "Outcome: ${DecimalFormat("#,###.00").format(it.outcome)}", style = MaterialTheme.typography.h6)
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Status:", style = MaterialTheme.typography.h6)
                        if (it.income-it.outcome > BigDecimal.ZERO)
                            Icon(modifier = Modifier,
                                tint = StatusBalance.Profit.color,
                                imageVector = StatusBalance.Profit.icon!!,
                                contentDescription = null)
                        else if (it.income-it.outcome == BigDecimal.ZERO)
                            Text(text = "=", color = StatusBalance.Balance.color,  style = MaterialTheme.typography.h4)
                        else
                            Icon(modifier = Modifier,
                                tint = StatusBalance.Loss.color,
                                imageVector = StatusBalance.Loss.icon!!,
                                contentDescription = null)
                    }
                    if(stateCollapse.value == it.id) {
                        Text(
                            textAlign = TextAlign.End,
                            text = "get detail and visualize data",
                            style = MaterialTheme.typography.body1,
                            fontStyle = FontStyle.Italic
                        )
                        Button(onClick = { navController.navigate(route = "${Navigation.VisualizeNote.link}/item/${it.id}") }) {
                            Text(text = "Get Visualize", style = MaterialTheme.typography.h5, fontStyle = FontStyle.Italic)
                        }
                    }
                }
            }
        }
    }
    Row {
        IconButton(onClick = { if(statePage.value-1 >= 0) {
            statePage.value-= 1

        }}) {
            Icon(imageVector = , contentDescription = "previous")
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .weight(1f))
        IconButton(onClick = {  }) {
            
        }
    }
}
