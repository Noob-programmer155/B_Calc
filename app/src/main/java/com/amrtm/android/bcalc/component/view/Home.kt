package com.amrtm.android.bcalc.component.view

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.rounded.EuroSymbol
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amrtm.android.bcalc.component.data.repository.Balance
import com.amrtm.android.bcalc.component.data.viewmodel.HomeViewModel
import com.amrtm.android.bcalc.ui.theme.BCalcTheme
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

//@Composable
//fun Home(
//    navController: NavHostController,
//    padding: Dp,
//    content: @Composable() (ColumnScope.()-> Unit)
//) {
//    Column (modifier = Modifier
//        .fillMaxWidth()
//        .padding(padding)) {
//        Row (modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center) {
//            Row (modifier = Modifier
//                .padding(0.dp)
//                .clip(RoundedCornerShape(50))
//                .background(MaterialTheme.colors.secondary.copy(alpha = .4f))
//                .height(30.dp)) {
//                Button(
//                    modifier = Modifier
//                        .padding(0.dp, 0.dp, 3.dp, 0.dp)
//                        .fillMaxHeight()
//                        .padding(0.dp),
//                    shape = RoundedCornerShape(50),
//                    contentPadding = PaddingValues(15.dp,0.dp),
//                    onClick = { navController.navigate(route = Navigation.Home.link) }
//                ) {
//                    Text(text = "Status")
//                }
//                Button(
//                    modifier = Modifier
//                        .padding(3.dp, 0.dp)
//                        .fillMaxHeight()
//                        .padding(0.dp),
//                    shape = RoundedCornerShape(50),
//                    contentPadding = PaddingValues(15.dp,0.dp),
//                    onClick = { navController.navigate(route = "${Navigation.Home.link}?type=note") }
//                ) {
//                    Text(text = "Notes")
//                }
//                Button(
//                    modifier = Modifier
//                        .padding(3.dp, 0.dp, 0.dp, 0.dp)
//                        .fillMaxHeight()
//                        .padding(0.dp),
//                    shape = RoundedCornerShape(50),
//                    contentPadding = PaddingValues(15.dp,0.dp),
//                    onClick = { navController.navigate(route = "${Navigation.Home.link}?type=item") }
//                ) {
//                    Text(text = "Items")
//                }
//            }
//        }
//        Column(modifier = Modifier.fillMaxWidth(), content = content)
//    }
//}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Home(
    padding: Dp,
    sizePage: DpSize = DpSize(LocalConfiguration.current.screenWidthDp.dp, LocalConfiguration.current.screenHeightDp.dp),
    numberPage: Int,
    content: @Composable (RowScope.() -> Unit)
) {
    val swipeState = rememberSwipeableState(0)
    val anchors = mapOf(*(0 until numberPage).toList().map { it.toFloat()*sizePage.width.value to it }.toTypedArray())
    Surface(
        modifier = Modifier
            .padding(padding)
            .fillMaxHeight()
            .swipeable(
                state = swipeState,
                anchors = anchors,
                orientation = Orientation.Horizontal,
                thresholds = { _, _ -> FractionalThreshold(.75f) }
            ),
    ) {
        Row(content = content)
    }
}

@Composable
fun BalanceCard (
    screenWidth: WindowWidthSizeClass,
    viewModel: HomeViewModel,
    modifier: Modifier
) {
    val storage by viewModel.balanceData.collectAsState()
    Box(
        modifier = modifier
    ) {
        BalanceCardItem(screenWidth = screenWidth, balance = storage)
    }
}

@Composable
fun BalanceCardItem (
    screenWidth: WindowWidthSizeClass,
    balance: Balance,
    stateCollapse: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    val onFinishedListener: MutableState<Boolean> = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                ),
                finishedListener = { _, _ -> onFinishedListener.value = !onFinishedListener.value }
            ).padding(15.dp,15.dp,15.dp,30.dp),
        backgroundColor = MaterialTheme.colors.secondaryVariant,
        contentColor = MaterialTheme.colors.onSecondary,
        shape = MaterialTheme.shapes.medium,
        elevation = 15.dp
    ) {
        Box(
            contentAlignment = Alignment.TopCenter
        ) {
            Icon(
                modifier = Modifier
                    .offset(
                        if (screenWidth >= WindowWidthSizeClass.Medium) 180.dp else 80.dp,
                        (-20).dp
                    )
                    .size(120.dp)
                    .rotate(45f),
                imageVector = Icons.Outlined.MonetizationOn, contentDescription = null,
                tint = MaterialTheme.colors.secondary
            )
            Icon(
                modifier = Modifier
                    .offset(
                        if (screenWidth >= WindowWidthSizeClass.Medium) 200.dp else 100.dp,
                        230.dp
                    )
                    .size(120.dp)
                    .rotate(-45f),
                imageVector = Icons.Filled.Payments, contentDescription = null,
                tint = MaterialTheme.colors.secondary
            )
            Icon(
                modifier = Modifier
                    .offset(
                        if (screenWidth >= WindowWidthSizeClass.Medium) (-180).dp else (-80).dp,
                        170.dp
                    )
                    .size(120.dp)
                    .rotate(15f),
                imageVector = Icons.Rounded.EuroSymbol, contentDescription = null,
                tint = MaterialTheme.colors.secondary
            )
            Column(
                modifier = Modifier
                    .padding(10.dp, 10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                if (screenWidth >= WindowWidthSizeClass.Medium) {
                    Text(
                        text = "Your Balance",
                        style = MaterialTheme.typography.h1,
                        fontSize = 50.sp,
                        modifier = Modifier.padding(0.dp,0.dp,0.dp,5.dp))
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column (modifier = Modifier.padding(0.dp, 0.dp, 15.dp, 0.dp)) {
                            Text(text = "Income:", style = MaterialTheme.typography.h6)
                            Text(
                                modifier = Modifier.fillMaxWidth(.4f),
                                text = "Rp. ${DecimalFormat("#,###.00").format(balance.income)}",
                                style = MaterialTheme.typography.h3,
                                textAlign = TextAlign.End
                            )
                            Text(text = "Outcome:", style = MaterialTheme.typography.h6)
                            Text(
                                modifier = Modifier.fillMaxWidth(.4f),
                                text = "Rp. ${DecimalFormat("#,###.00").format(balance.outcome)}",
                                style = MaterialTheme.typography.h3,
                                textAlign = TextAlign.End
                            )
                        }
                        Divider(modifier = Modifier
                            .fillMaxHeight(.8f)
                            .width(1.dp),color = MaterialTheme.colors.onSecondary)
                        Column(modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Status: ${balance.status?.name}",
                                    style = MaterialTheme.typography.h6
                                )
                                if (balance.status?.icon != null)
                                    Icon(
                                        modifier = Modifier,
                                        tint = balance.status.color,
                                        imageVector = balance.status.icon,
                                        contentDescription = null
                                    )
                                else
                                    Text(
                                        modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp),
                                        text = "=",
                                        color = balance.status?.color!!,
                                        style = MaterialTheme.typography.h1
                                    )
                            }
                            Text(text = "Last Update: ${SimpleDateFormat("dd MMM yyyy", Locale.US)
                                .format(balance.lastUpdate!!)}", style = MaterialTheme.typography.h6)
                            Text(
                                text = "Notes Count: ${balance.noteCount}",
                                style = MaterialTheme.typography.h6
                            )
                            Text(
                                text = "Items Count: ${balance.itemsCount}",
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }
                } else {
                    Text(text = "Your Balance", style = MaterialTheme.typography.h1, modifier = Modifier.padding(15.dp,0.dp,0.dp,15.dp))
                    Text(text = "Income:", style = MaterialTheme.typography.h6)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Rp. ${DecimalFormat("#,###.00").format(balance.income)}",
                        style = MaterialTheme.typography.h3,
                        textAlign = TextAlign.End
                    )
                    Text(text = "Outcome:", style = MaterialTheme.typography.h6)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Rp. ${DecimalFormat("#,###.00").format(balance.outcome)}",
                        style = MaterialTheme.typography.h3,
                        textAlign = TextAlign.End
                    )
                    Text(text = "Last Update:", style = MaterialTheme.typography.h6)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = SimpleDateFormat(
                            "dd MMM yyyy",
                            Locale.US
                        ).format(balance.lastUpdate!!),
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.End
                    )
                    if (stateCollapse.value) {
                        Divider(color = MaterialTheme.colors.onPrimary)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Notes Count: ${balance.noteCount}",
                                style = MaterialTheme.typography.h6
                            )
                            Text(
                                text = "Items Count: ${balance.itemsCount}",
                                style = MaterialTheme.typography.h6
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Status: ${balance.status?.name}",
                                    style = MaterialTheme.typography.h6
                                )
                                if (balance.status?.icon != null)
                                    Icon(
                                        modifier = Modifier.padding(7.dp,0.dp,0.dp,0.dp),
                                        tint = balance.status.color,
                                        imageVector = balance.status.icon,
                                        contentDescription = null
                                    )
                                else
                                    Text(
                                        modifier = Modifier.padding(7.dp,0.dp,0.dp,0.dp),
                                        text = "=",
                                        color = balance.status?.color!!,
                                        style = MaterialTheme.typography.h1
                                    )
                            }
                        }
                    }
                    val animateRotate by animateFloatAsState(
                        targetValue = if (onFinishedListener.value) 180f else 0f,
                        animationSpec = tween(
                            durationMillis = 200,
                            easing = LinearOutSlowInEasing
                        )
                    )
                    IconButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            stateCollapse.value = !stateCollapse.value
                        },
                        enabled = true
                    ) {
                        Icon(
                            modifier = Modifier.rotate(animateRotate),
                            imageVector = Icons.Filled.ExpandMore,
                            contentDescription = "lihat lainnya"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, widthDp = 480, heightDp = 800)
@Composable
fun HomePreview() {
    val windowSizeClass = LocalConfiguration.current
    val width = windowSizeClass.screenWidthDp.dp
    val height = windowSizeClass.screenHeightDp.dp
    BCalcTheme {
        // A surface containcom.amrtm.android.bcalc.component.er using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.surface
        ) {
            Home(padding = 10.dp, numberPage = 1) {
                Surface(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val income = BigDecimal.valueOf(21000000)
                    val outcome = BigDecimal.valueOf(15000000)
                    val profit = getStatusBalance(income,outcome)
                    BalanceCardItem(screenWidth = WindowSizeClass.calculateFromSize(DpSize(width,height)).widthSizeClass, balance = Balance(
                        id = 12L,
                        income = income,
                        outcome = outcome,
                        profit = profit.second,
                        lastUpdate = Date(),
                        status = profit.first,
                        noteCount = 120,
                        itemsCount = 150
                    )
                    )
                }
            }
        }
    }
}
