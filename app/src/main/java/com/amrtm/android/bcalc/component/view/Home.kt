package com.amrtm.android.bcalc.component.view

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.amrtm.android.bcalc.component.data.Navigation
import com.amrtm.android.bcalc.component.data.viewmodel.HomeViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Home(
    navController: NavHostController,
    padding: Dp,
    content: @Composable () -> Unit
) {
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(padding)) {
        Row (modifier = Modifier
            .padding(40.dp)
            .fillMaxWidth()
            .padding(0.dp), horizontalArrangement = Arrangement.Center) {
            Row (modifier = Modifier.clip(RoundedCornerShape(50)).background(MaterialTheme.colors.secondary.copy(alpha = .8f))) {
                Button(
                    shape = RoundedCornerShape(50),
                    onClick = { navController.navigate(route = Navigation.Home.link) }
                ) {
                    Text(text = "Status")
                }
                Button(
                    shape = RoundedCornerShape(50),
                    onClick = { navController.navigate(route = "${Navigation.Home.link}/note") }
                ) {
                    Text(text = "Notes")
                }
                Button(
                    shape = RoundedCornerShape(50),
                    onClick = { navController.navigate(route = "${Navigation.Home.link}/item") }
                ) {
                    Text(text = "Items")
                }
            }
        }
        content
    }
}

@Composable
fun BalanceCard (
    screenWidth: WindowWidthSizeClass,
    viewModel: HomeViewModel
) {
    val storage by viewModel.balanceData.collectAsState()
    Card(
        modifier = Modifier
            .padding(0.dp, 25.dp, 0.dp, 70.dp)
            .fillMaxWidth()
            .padding(15.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = MaterialTheme.shapes.large,
        backgroundColor = MaterialTheme.colors.secondaryVariant,
        contentColor = MaterialTheme.colors.onSecondary,
        elevation = 70.dp
    ) {
        Column {
            Text(text = "Your Balance", style = MaterialTheme.typography.h3, modifier = Modifier.padding(0.dp,0.dp,0.dp,15.dp))
            if (screenWidth >= WindowWidthSizeClass.Medium) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(
                        modifier = Modifier
                            .aspectRatio(.5f)
                            .padding(0.dp, 0.dp, 15.dp, 0.dp)
                    ) {
                        Text(text = "Income:", style = MaterialTheme.typography.h6)
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Rp. ${DecimalFormat("#,###.00").format(storage.income)}",
                            style = MaterialTheme.typography.h4,
                            textAlign = TextAlign.End
                        )
                        Text(text = "Outcome:", style = MaterialTheme.typography.h6)
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Rp. ${DecimalFormat("#,###.00").format(storage.outcome)}",
                            style = MaterialTheme.typography.h4,
                            textAlign = TextAlign.End
                        )
                    }
                    Column(
                        modifier = Modifier
                            .aspectRatio(.5f)
                            .padding(15.dp, 0.dp, 0.dp, 0.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Status: ${storage.status?.name}",
                                style = MaterialTheme.typography.h6
                            )
                            if (storage.status?.icon != null)
                                Icon(
                                    modifier = Modifier,
                                    tint = storage.status?.color!!,
                                    imageVector = storage.status?.icon!!,
                                    contentDescription = null
                                )
                            else
                                Text(
                                    text = "=",
                                    color = storage.status?.color!!,
                                    style = MaterialTheme.typography.h4
                                )
                        }
                        Text(text = "Last Update:", style = MaterialTheme.typography.h6)
                        Text(
                            text = SimpleDateFormat(
                                "dd MMM yyyy",
                                Locale.US
                            ).format(storage.lastUpdate!!),
                            style = MaterialTheme.typography.h5
                        )
                        Text(
                            text = "Notes Count: ${storage.noteCount}",
                            style = MaterialTheme.typography.h6
                        )
                        Text(
                            text = "Items Count: ${storage.itemsCount}",
                            style = MaterialTheme.typography.h6
                        )
                    }
                }
            } else {
                Text(text = "Income:", style = MaterialTheme.typography.h6)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Rp. ${DecimalFormat("#,###.00").format(storage.income)}",
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.End
                )
                Text(text = "Outcome:", style = MaterialTheme.typography.h6)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Rp. ${DecimalFormat("#,###.00").format(storage.outcome)}",
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.End
                )
                Text(text = "Last Update:", style = MaterialTheme.typography.h6)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.US
                    ).format(storage.lastUpdate!!),
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.End
                )
                if (viewModel.collapseCard.value == -1) {
                    Divider()
                    Text(
                        text = "Notes Count: ${storage.noteCount}",
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = "Items Count: ${storage.itemsCount}",
                        style = MaterialTheme.typography.h6
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Status: ${storage.status?.name}",
                            style = MaterialTheme.typography.h6
                        )
                        if (storage.status?.icon != null)
                            Icon(
                                modifier = Modifier,
                                tint = storage.status?.color!!,
                                imageVector = storage.status?.icon!!,
                                contentDescription = null
                            )
                        else
                            Text(
                                text = "=",
                                color = storage.status?.color!!,
                                style = MaterialTheme.typography.h4
                            )
                    }
                }
                IconButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    onClick = {
                        viewModel.setCollapseCard(if (viewModel.collapseCard.value == -1) -2 else -1)
                    },
                    enabled = true
                ) {
                    Icon(
                        modifier = Modifier.rotate(if (viewModel.collapseCard.value == -1) 180f else 0f).animateContentSize(
                            animationSpec = tween(
                                durationMillis = 1000,
                                easing = FastOutLinearInEasing
                            )
                        ),
                        imageVector = Icons.Filled.ExpandMore,
                        contentDescription = "lihat lainnya"
                    )
                }
            }
        }
    }
}