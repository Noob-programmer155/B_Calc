package com.amrtm.android.bcalc.component.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.amrtm.android.bcalc.component.data.*
import com.amrtm.android.bcalc.component.data.repository.Item
import com.amrtm.android.bcalc.component.data.viewmodel.ItemViewModel
import com.amrtm.android.bcalc.ui.theme.BCalcTheme
import java.text.DecimalFormat
import java.util.*

@Composable
fun ItemsList(
    storage: ItemViewModel,
    windowSize: WindowWidthSizeClass,
    navController: NavHostController,
    modifier: Modifier
) {
    val items = storage.pagingData.collectAsLazyPagingItems()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Item Data",
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onBackground
        )
        Text(
            modifier = Modifier.padding(0.dp,10.dp,0.dp,0.dp),
            text = "Check your item history to get your business insight",
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Light),
            color = if (isSystemInDarkTheme()) Color(.95f,.95f,.95f) else Color(.4f,.4f,.4f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "* click item to see details in visualization",
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
            color = if (isSystemInDarkTheme()) Color(.8f,.8f,.8f) else Color(.55f,.55f,.55f)
        )
        Divider(modifier = Modifier.padding(15.dp,0.dp))
        ItemsView(
            modifier = Modifier
                .padding(0.dp)
                .fillMaxHeight()
                .weight(1f)
                .padding(0.dp, 0.dp, 0.dp, 80.dp),
            items = items,
            navController = navController,
            width = windowSize
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemsView(
    items: LazyPagingItems<Item>,
    navController: NavHostController,
    stateRetry: MutableState<Int> = remember { mutableStateOf(0) },
    width: WindowWidthSizeClass,
    colors: Map<Char, Color> = colorGenerate(),
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(0.dp,5.dp)
    ) {
        itemsIndexed(items){ _,it ->
            Card(
                onClick = {
                    navController.navigate(route = "${Navigation.VisualizeItem(
                        if (width == WindowWidthSizeClass.Compact) 10 else if (width == WindowWidthSizeClass.Medium) 20 else 30
                    ).link}?name=${it?.name}")
                },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                shape = MaterialTheme.shapes.medium,
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                elevation = 5.dp,
            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 5.dp, 0.dp)
                            .background(
                                color = colors[it?.name
                                    ?.uppercase()
                                    ?.get(0)]!!.copy(alpha = 1f)
                            )
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = it?.name?.get(0)?.uppercase()!!, style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)
                    }
                    Text(text = it?.name!!, style = MaterialTheme.typography.h6)
                    Column(
                        modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp)
                    ) {
                        Text(text = "Latest Cost:", style = MaterialTheme.typography.caption)
                        Text(text = DecimalFormat("#,###.00").format(it.sellCost), style = MaterialTheme.typography.body1)
                    }
                    if (width >= WindowWidthSizeClass.Medium) {
                        Column(
                            modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp)
                        ) {
                            Text(text = "Latest Buy Cost:", style = MaterialTheme.typography.caption)
                            Text(text = DecimalFormat("#,###.00").format(it.buyCost), style = MaterialTheme.typography.body1)
                        }
                    }
                    Column(
                        modifier = Modifier.padding(10.dp,0.dp,0.dp,0.dp)
                    ) {
                        Text(text = "Stock:", style = MaterialTheme.typography.caption)
                        Text(text = "${it.stock}", style = MaterialTheme.typography.body1)
                    }
                }
            }
        }
        when(items.loadState.refresh) {
            is LoadState.Loading -> item {
                Box(modifier = Modifier
                    .padding(30.dp)
                    .fillMaxSize()
                    .padding(20.dp),contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = if(isSystemInDarkTheme()) Color.White else Color(0xFF3C79F5))
                }
            }
            is LoadState.Error -> item {
                Column (
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (stateRetry.value <= 3) {
                        Icon(imageVector = Icons.Rounded.ErrorOutline, contentDescription = "error")
                        Text(text = "there is error when loading the data \n would you like to retry it")
                        Button(onClick = {
                            items.retry()
                            stateRetry.value += 1
                        }) {
                            Icon(
                                modifier = Modifier.padding(0.dp,0.dp,10.dp,0.dp),
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "retry")
                            Text(text = "Retry")
                        }
                    } else {
                        Icon(imageVector = Icons.Rounded.ErrorOutline, contentDescription = "error")
                        Text(text = "its still getting error, maybe will fixed by refresh it")
                        Button(onClick = {
                            items.refresh()
                            stateRetry.value = 0
                        }) {
                            Icon(
                                modifier = Modifier.padding(0.dp,0.dp,10.dp,0.dp),
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "retry")
                            Text(text = "Refresh")
                        }
                    }
                }
            }
            is LoadState.NotLoading -> item {
                if (items.itemCount <= 0)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Empty note list, you should add new Note and Item to see this page again")
                    }
            }
            else -> {}
        }
        when(items.loadState.append) {
            is LoadState.Loading -> item {
                Box(modifier = Modifier
                    .padding(30.dp)
                    .fillMaxWidth()
                    .padding(20.dp),contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = if(isSystemInDarkTheme()) Color.White else Color(0xFF3C79F5))
                }
            }
            is LoadState.Error -> item {
                Column (
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (stateRetry.value <= 3) {
                        Icon(imageVector = Icons.Rounded.ErrorOutline, contentDescription = "error")
                        Text(text = "there is error when loading the data \n would you like to retry it")
                        Button(onClick = {
                            items.retry()
                            stateRetry.value += 1
                        }) {
                            Icon(
                                modifier = Modifier.padding(0.dp,0.dp,10.dp,0.dp),
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "retry")
                            Text(text = "Retry")
                        }
                    } else {
                        Icon(imageVector = Icons.Rounded.ErrorOutline, contentDescription = "error")
                        Text(text = "its still getting error, maybe will fixed by refresh it")
                        Button(onClick = {
                            items.refresh()
                            stateRetry.value = 0
                        }) {
                            Icon(
                                modifier = Modifier.padding(0.dp,0.dp,10.dp,0.dp),
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "retry")
                            Text(text = "Refresh")
                        }
                    }
                }
            }
            else -> {}
        }
    }
}



@Preview(showBackground = true, widthDp = 480, heightDp = 800)
@Composable
fun ItemsPreview() {
    val navController: NavHostController = rememberNavController()
    BCalcTheme {
        // A surface containcom.amrtm.android.bcalc.component.er using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.surface
        ) {
            Home(padding = 30.dp, numberPage = 1) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
//                    val data = PagingData.from(listOf<Item>(
//                        Item(
//                            1,2,"Straw A", BigDecimal.valueOf(10000),20,100,250,350,
//                            BigDecimal.valueOf(10000).multiply(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf((100 - 20))),Date()
//                        ),
//                        Item(
//                            1,2,"Straw A", BigDecimal.valueOf(10000),20,100,250,350,
//                            BigDecimal.valueOf(10000).multiply(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf((100 - 20))),Date()
//                        ),
//                        Item(
//                            1,2,"Straw A", BigDecimal.valueOf(10000),20,100,250,350,
//                            BigDecimal.valueOf(10000).multiply(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf((100 - 20))),Date()
//                        ),
//                        Item(
//                            1,2,"Straw A", BigDecimal.valueOf(10000),20,100,250,350,
//                            BigDecimal.valueOf(10000).multiply(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf((100 - 20))),Date()
//                        ),
//                        Item(
//                            1,2,"Straw A", BigDecimal.valueOf(10000),20,100,250,350,
//                            BigDecimal.valueOf(10000).multiply(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf((100 - 20))),Date()
//                        )
//                    ))
//                    ItemsView(items = flowOf(data).collectAsLazyPagingItems(), navController = navController)
                }
            }
        }
    }
}