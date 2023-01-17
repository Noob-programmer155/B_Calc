package com.amrtm.android.bcalc.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amrtm.android.bcalc.component.data.DataStorage
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Home(
    screenWidth: WindowWidthSizeClass,
    storage: DataStorage,

) {
    val collapseCard = remember { mutableStateOf(-2)}
    BalanceCard(screenWidth = screenWidth, storage = storage, collapseCard)
    CardAddLayout()
}

@Composable
fun BalanceCard (
    screenWidth: WindowWidthSizeClass,
    storage: DataStorage,
    stateCollapse: MutableState<Int>
) {
    Card(
        modifier = Modifier
            .padding(0.dp, 25.dp, 0.dp, 70.dp)
            .fillMaxWidth()
            .padding(15.dp)
            .animateContentSize(animationSpec = tween(
                durationMillis = 1000,
                easing = LinearOutSlowInEasing
            )),
        shape = MaterialTheme.shapes.large,
        backgroundColor = MaterialTheme.colors.secondaryVariant,
        contentColor = MaterialTheme.colors.onSecondary,
        elevation = 70.dp
    ) {
        Column {
            Text(text = "Your Balance", style = MaterialTheme.typography.h3, modifier = Modifier.padding(0.dp,0.dp,0.dp,15.dp))
            if (screenWidth >= WindowWidthSizeClass.Medium){
                Row {
                    Column (modifier = Modifier
                        .aspectRatio(.5f)
                        .padding(0.dp, 0.dp, 15.dp, 0.dp)){
                        Text(text = "Income:", style = MaterialTheme.typography.h6)
                        Text(modifier = Modifier.fillMaxWidth(),
                            text = "Rp. ${DecimalFormat("#,###.00").format(storage.balance.income)}",
                            style = MaterialTheme.typography.h4,
                            textAlign = TextAlign.End
                        )
                        Text(text = "Outcome:", style = MaterialTheme.typography.h6)
                        Text(modifier = Modifier.fillMaxWidth(),
                            text = "Rp. ${DecimalFormat("#,###.00").format(storage.balance.outcome)}",
                            style = MaterialTheme.typography.h4,
                            textAlign = TextAlign.End
                        )
                    }
                    Column (modifier = Modifier
                        .aspectRatio(.5f)
                        .padding(15.dp, 0.dp, 0.dp, 0.dp), horizontalAlignment = Alignment.End){
                        Row (verticalAlignment = Alignment.CenterVertically){
                            Text(text = "Status: ${storage.balance.status.name}", style = MaterialTheme.typography.h6)
                            if (storage.balance.status.icon != null)
                                Icon(modifier = Modifier,
                                    tint = storage.balance.status.color,
                                    imageVector = storage.balance.status.icon!!,
                                    contentDescription = null)
                            else
                                Text(text = "=", color = storage.balance.status.color,  style = MaterialTheme.typography.h4)
                        }
                        Text(text = "Last Update:", style = MaterialTheme.typography.h6)
                        Text(text = SimpleDateFormat("dd MMM yyyy", Locale.US).format(storage.balance.lastUpdate), style = MaterialTheme.typography.h5)
                        Text(text = "Notes Count: ${storage.balance.noteCount}", style = MaterialTheme.typography.h6)
                        Text(text = "Items Count: ${storage.balance.itemsCount}", style = MaterialTheme.typography.h6)
                    }
                }
            } else {
                Text(text = "Income:", style = MaterialTheme.typography.h6)
                Text(modifier = Modifier.fillMaxWidth(),
                    text = "Rp. ${DecimalFormat("#,###.00").format(storage.balance.income)}",
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.End
                )
                Text(text = "Outcome:", style = MaterialTheme.typography.h6)
                Text(modifier = Modifier.fillMaxWidth(),
                    text = "Rp. ${DecimalFormat("#,###.00").format(storage.balance.outcome)}",
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.End
                )
                Text(text = "Last Update:", style = MaterialTheme.typography.h6)
                Text(modifier = Modifier.fillMaxWidth(),
                    text = SimpleDateFormat("dd MMM yyyy", Locale.US).format(storage.balance.lastUpdate),
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.End
                )
                if (stateCollapse.value == -1) {
                    Divider()
                    Text(text = "Notes Count: ${storage.balance.noteCount}", style = MaterialTheme.typography.h6)
                    Text(text = "Items Count: ${storage.balance.itemsCount}", style = MaterialTheme.typography.h6)
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Text(text = "Status: ${storage.balance.status.name}", style = MaterialTheme.typography.h6)
                        if (storage.balance.status.icon != null)
                            Icon(modifier = Modifier,
                                tint = storage.balance.status.color,
                                imageVector = storage.balance.status.icon!!,
                                contentDescription = null)
                        else
                            Text(text = "=", color = storage.balance.status.color,  style = MaterialTheme.typography.h4)
                    }
                }
                TextButton(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    onClick = { stateCollapse.value = if(stateCollapse.value == -1) -2 else -1 },
                    enabled = true
                ) {
                    Icon(imageVector = if (stateCollapse.value == -1) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = "lihat lainnya")
                }
            }
        }
    }
}