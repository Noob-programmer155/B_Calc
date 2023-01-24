package com.amrtm.android.bcalc.component.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.amrtm.android.bcalc.ViewMain
import com.amrtm.android.bcalc.component.data.*
import com.amrtm.android.bcalc.component.data.viewmodel.ItemViewModel
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemsList(
    storage: ItemViewModel = viewModel(factory = ViewMain.Factory),
    windowSize: WindowWidthSizeClass,
    navController: NavHostController
) {
    storage.pageSize.value = (if (windowSize == WindowWidthSizeClass.Compact) 10 else if (windowSize == WindowWidthSizeClass.Medium) 20 else 30)
    val items = storage.pagingData.collectAsLazyPagingItems()
    Row (verticalAlignment = Alignment.CenterVertically) {
        SearchField(view = storage)
        Filter(view = storage)
    }
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        itemsIndexed(items){ _,it ->
            Card(
                onClick = {
                    navController.navigate(route = "${Navigation.VisualizeItem.link}/item/${it?.id}")
                },
                enabled = true,
                modifier = Modifier
                    .padding(0.dp, 25.dp, 0.dp, 0.dp)
                    .fillMaxWidth()
                    .padding(15.dp),
                shape = MaterialTheme.shapes.medium,
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                elevation = 20.dp,
                border = BorderStroke(5.dp, MaterialTheme.colors.onBackground)
            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 25.dp, 0.dp)
                            .background(storage.colors[it?.name?.uppercase()?.get(0)]!!)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = it?.name?.get(0)?.uppercase()!!, style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)
                    }
                    Text(text = it?.name!!, style = MaterialTheme.typography.h6)
                    Column {
                        Text(text = "Latest Cost:", style = MaterialTheme.typography.caption)
                        Text(text = DecimalFormat("#,###.00").format(it.cost), style = MaterialTheme.typography.h6)
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(30.dp, 0.dp, 0.dp, 0.dp),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                        text = "(click to see details in visualization)"
                    )
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
                    modifier = Modifier.fillParentMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(text = "there is error when loading the data")
                    Icon(imageVector = Icons.Rounded.ErrorOutline, contentDescription = "error")
                }
            }
            else -> {}
        }
        when(items.loadState.append) {
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
                    modifier = Modifier.fillParentMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(text = "there is error when loading the data")
                    Icon(imageVector = Icons.Rounded.ErrorOutline, contentDescription = "error")
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun SearchField(
    view: ItemViewModel,
    stateSearchItem: MutableState<String> = remember { mutableStateOf("") },
) {
    OutlinedTextField(
        value = stateSearchItem.value,
        placeholder = { Text(text = "Search...", style = MaterialTheme.typography.subtitle1)},
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            view.onSearch(stateSearchItem.value)
        }),
        onValueChange = { stateSearchItem.value = it },
        trailingIcon = { SearchButton(view = view,stateSearchItem = stateSearchItem)}
    )
}

@Composable
private fun SearchButton(
    view: ItemViewModel,
    stateSearchItem: MutableState<String>
) {
    IconButton(onClick = {
        view.onSearch(stateSearchItem.value)
    }) {
        Icon(imageVector = Icons.Rounded.Search, contentDescription = "search item")
    }
}

