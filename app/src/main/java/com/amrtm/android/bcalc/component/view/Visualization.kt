package com.amrtm.android.bcalc.component.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Square
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.amrtm.android.bcalc.ViewMain
import com.amrtm.android.bcalc.component.data.ChartColor
import com.amrtm.android.bcalc.component.data.Navigation
import com.amrtm.android.bcalc.component.data.VisualDataAttributeLoader
import com.amrtm.android.bcalc.component.data.repository.Item
import com.amrtm.android.bcalc.component.data.repository.ItemHistory
import com.amrtm.android.bcalc.component.data.viewmodel.HomeViewModel
import com.amrtm.android.bcalc.component.data.viewmodel.VisualViewModel
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class Visualization {
    enum class TypeStatus {
        COST,
        SOLD,
        STOCK,
        TOTAL
    }

    private val minXData: MutableState<Long> = mutableStateOf(0L)
    private val maxXData: MutableState<Long> = mutableStateOf(0L)

    object Load {
        val type = listOf(TypeStatus.COST,TypeStatus.SOLD,TypeStatus.STOCK,TypeStatus.TOTAL)
        val title = listOf("Cost Item","Sold Item","Stock Item","Total")
        val xTitle = listOf("Date History","Date History","Date History","Date History")
        val yTitle = listOf("Cost History","Count Sold History","Stock Item History","Total History")
        val label = listOf(
            DataLabel(Color(0xFFFC7300),"Cost",true),
            DataLabel(Color.Green,"Count Sold",true),
            DataLabel(Color.Blue,"In Stock",true),
            DataLabel(Color.Cyan,"Total Income",true),
        )
        val legends = listOf(
            Legend(Icons.Rounded.Square,VisualDataAttributeLoader.defaultColor()[0].mainColor,"Cost History"),
            Legend(Icons.Rounded.Square,VisualDataAttributeLoader.defaultColor()[1].mainColor,"Sold History"),
            Legend(Icons.Rounded.Square,VisualDataAttributeLoader.defaultColor()[2].mainColor,"Stock History"),
            Legend(Icons.Rounded.Square,VisualDataAttributeLoader.defaultColor()[3].mainColor,"Total History")
        )
    }

    @Composable
    fun CreateVisualization (
        name: String?,
        view: VisualViewModel = viewModel(factory = ViewMain.Factory),
        navController: NavHostController,
        width: WindowWidthSizeClass
    ) {
        view.setName(name!!)
        if(name.isBlank()) {
            WelcomePage(
                view = view,
                modifier = Modifier.padding(15.dp),
                navController = navController,
                width = width
            )
        } else {
            val items = view.itemDataVisual().collectAsState(initial = listOf())
            val pointDatas = getDataItem(items.value,TypeStatus.COST,TypeStatus.SOLD,TypeStatus.STOCK,TypeStatus.TOTAL)
            val points = VisualDataAttributeLoader.bindDataPointsToColor(pointDatas,Load.title,Load.xTitle,Load.yTitle,*Load.type.toTypedArray())
            CreateVisualizationCard(
                typesButton = Load.type,
                modifier = Modifier.padding(15.dp),
                items = points
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun WelcomePage(
        view: VisualViewModel,
        homeView: HomeViewModel = viewModel(factory = ViewMain.Factory),
        navController: NavHostController,
        modifier: Modifier,
        width: WindowWidthSizeClass,
        searchChange: MutableState<String> = rememberSaveable { mutableStateOf("") },
        colors: Map<Char, Color> = colorGenerate()
    ) {
        val items = view.pagingData.collectAsLazyPagingItems()
        val balance by homeView.balanceData.collectAsState()
        Column (
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                Text(text = "Welcome To", style = MaterialTheme.typography.h6)
                Text(text = "Data Visualization", style = MaterialTheme.typography.h4)
                Text(
                    text = "In here you can see your data item history and make your future decision of your plan to grow your business",
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = "(You can see your item history data in category cost to see " +
                            "cost history, sold item history, stock item history, and total income in one item history)",
                    style = MaterialTheme.typography.caption
                )
                Card(
                    modifier = Modifier
                        .padding(10.dp, 30.dp)
                        .padding(15.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = 15.dp
                ) {
                    Column {
                        Text(text = "Your Balance Status", style = MaterialTheme.typography.h3)
                        Row {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                text = "Income:", style = MaterialTheme.typography.h6,
                                textAlign = TextAlign.Start
                            )
                            Text(text = "Rp. ${DecimalFormat("#,###.00").format(balance.income)}", style = MaterialTheme.typography.h5)
                        }
                        Row {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                text = "Outcome:", style = MaterialTheme.typography.h6,
                                textAlign = TextAlign.Start
                            )
                            Text(text = "Rp. ${DecimalFormat("#,###.00").format(balance.outcome)}", style = MaterialTheme.typography.h5)
                        }
                        Row {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                text = "Profit:", style = MaterialTheme.typography.h6,
                                textAlign = TextAlign.Start
                            )
                            Text(text = "Rp. ${DecimalFormat("#,###.00").format(balance.income?.min(balance.outcome) ?: BigDecimal.ZERO)}", style = MaterialTheme.typography.h5)
                        }
                        Text(text = "Total Notes: ${balance.noteCount}", style = MaterialTheme.typography.body1)
                        Text(text = "Total Items: ${balance.itemsCount}", style = MaterialTheme.typography.body1)
                    }
                }
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp, 0.dp, 10.dp))
                Row {
//                    SearchField(
//                        modifier = Modifier,
//                        onSearch = {
//                            view.onSearch(searchChange.value)
//                        },
//                        onSearchKeyboard = {
//                            view.onSearch(searchChange.value)
//                        },
//                        stateSearchItem = searchChange
//                    )
//                    Filter(view = view)
                }
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 10.dp, 0.dp, 20.dp))
                LazyColumn {
                    itemsIndexed(items) {_,it ->
                        Card(
                            onClick = {
                                navController.navigate(route = "${Navigation.VisualizeItem().link}?name=${it?.name}")
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
                                        .background(colors[it?.name?.uppercase()?.get(0)]!!)
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = it?.name?.get(0)?.uppercase()!!, style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)
                                }
                                Text(text = it?.name!!, style = MaterialTheme.typography.h6)
                                Column {
                                    Text(text = "Latest Cost:", style = MaterialTheme.typography.caption)
                                    Text(text = DecimalFormat("#,###.00").format(it.sellCost), style = MaterialTheme.typography.h6)
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
                                .fillMaxWidth()
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
        }
    }

    @Composable
    private fun CreateVisualizationCard(
        typesButton: List<TypeStatus>,
        modifier: Modifier,
        items:  Map<TypeStatus,DataItem>,
        state: MutableState<Int> = remember { mutableStateOf(-1) }
    ) {
        Card(
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            elevation = 15.dp
        ) {
            Column {
                Row(modifier.padding(0.dp,10.dp,0.dp,20.dp)) {
                    OutlinedButton(
                        modifier = Modifier
                            .padding(15.dp)
                            .clip(RoundedCornerShape(50))
                            .padding(0.dp),
                        onClick = { state.value = -1 }
                    ) {
                        Text(text = "all")
                    }
                    typesButton.forEachIndexed {i,it ->
                        OutlinedButton(
                            modifier = Modifier
                                .padding(15.dp)
                                .clip(RoundedCornerShape(50))
                                .padding(0.dp),
                            onClick = { state.value = i }
                        ) {
                            Text(text = it.name.lowercase())
                        }
                    }
                }
                CreateVisualization(
                    data = items.values as List<DataItem>,
                    dataLabel = Load.label.map { listOf(it) },
                    state = state,
                    legends = Load.legends.map { listOf(it) }
                )
            }
        }
    }

    @Composable
    private fun CreateVisualization (
        data: List<DataItem>,
        state: MutableState<Int>,
        dataLabel: List<List<DataLabel>>,
        legends: List<List<Legend>>
    ) {
        val totalWidth = remember { mutableStateOf(0) }
        val xOffset = remember { mutableStateOf(0f) }
        val cardWidth = remember { mutableStateOf(0) }
        val cardHeight = remember { mutableStateOf(100.dp) }
        val visibility = remember { mutableStateOf(false) }
        val points = remember { mutableStateOf(listOf<DataPoint>()) }
        val density = LocalDensity.current
        val padding = 16.dp
        Column {
            Text(text = data[state.value].title, style = MaterialTheme.typography.h4)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = Modifier.rotate(90f),text = data[state.value].yTitle, maxLines = 3, style = MaterialTheme.typography.caption)
                LineGraph(
                    plot = LinePlot(
                        if (state.value >= 0)
                            listOf(
                                LinePlot.Line(
                                    data[state.value].data,
                                    LinePlot.Connection(data[state.value].color.mainColor),
                                    LinePlot.Intersection(data[state.value].color.interceptionColor),
                                    LinePlot.Highlight { center ->
                                        val color = data[state.value].color.pointColor1
                                        drawCircle(color, 9.dp.toPx(), center, alpha = 0.3f)
                                        drawCircle(color, 6.dp.toPx(), center)
                                        drawCircle(data[state.value].color.pointColor2, 3.dp.toPx(), center)
                                    },
                                    LinePlot.AreaUnderLine(data[state.value].color.areaColor)
                                )
                            )
                        else
                            data.map {
                                LinePlot.Line(
                                    it.data,
                                    LinePlot.Connection(it.color.mainColor),
                                    LinePlot.Intersection(it.color.interceptionColor),
                                    LinePlot.Highlight { center ->
                                        val color = it.color.pointColor1
                                        drawCircle(color, 9.dp.toPx(), center, alpha = 0.3f)
                                        drawCircle(color, 6.dp.toPx(), center)
                                        drawCircle(it.color.pointColor2, 3.dp.toPx(), center)
                                    },
                                    LinePlot.AreaUnderLine(it.color.areaColor)
                                )
                            }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(MaterialTheme.colors.background),
                    onSelectionStart = { visibility.value = true },
                    onSelectionEnd = { visibility.value = false }
                ) {x, pts ->
                    val cWidth = cardWidth.value.toFloat()
                    var xCenter = x + padding.toPx(density)
                    xCenter = when {
                        xCenter + cWidth / 2f > totalWidth.value -> totalWidth.value - cWidth
                        xCenter - cWidth / 2f < 0f -> 0f
                        else -> xCenter - cWidth / 2f
                    }
                    xOffset.value = xCenter
                    points.value = pts
                }
            }
            Text(text = if (state.value >= 0) data[state.value].xTitle else data[0].xTitle,maxLines = 3, style = MaterialTheme.typography.caption)
        }
        Box(modifier = Modifier.height(cardHeight.value)) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(60.dp),
                contentPadding = PaddingValues(10.dp),
                content = {
                    itemsIndexed(if (state.value >= 0) legends[state.value] else legends.map { it[0] }) {_,it ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = it.icon, contentDescription = null, tint = it.color)
                            Text(text = it.name)
                        }
                    }
                }
            )
            if (visibility.value) {
                PopUpLabel(
                    title = if (state.value >= 0) data[state.value].title else "Item History",
                    cardHeight = cardHeight,
                    cardWidth = cardWidth,
                    xOffset = xOffset,
                    dataLabel = if (state.value >= 0) dataLabel[state.value] else dataLabel.map { it[0] },
                    items = points,
                    density = density
                )
            }
        }
    }

    @Composable
    private fun PopUpLabel(
        title: String,
        cardWidth: MutableState<Int>,
        cardHeight: MutableState<Dp>,
        xOffset: MutableState<Float>,
        dataLabel: List<DataLabel>,
        items: MutableState<List<DataPoint>>,
        density: Density
    ) {
        Surface(
            modifier = Modifier
                .width(200.dp)
                .onGloballyPositioned {
                    cardWidth.value = it.size.width
                    cardHeight.value = toDp(it.size.height, density).dp
                }
                .graphicsLayer(translationX = xOffset.value),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFEAE0DA)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                item {
                    Text(text = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(convertDayOfYearToDate(items.value[0].x,minXData.value)), textAlign = TextAlign.End, style = MaterialTheme.typography.caption)
                    Text(text = title, textAlign = TextAlign.Start, style = MaterialTheme.typography.h4)
                    Divider(modifier = Modifier.padding(15.dp,0.dp))
                }
                itemsIndexed(dataLabel) {i,it ->
                    Row {
                        Icon(
                            modifier = Modifier.padding(0.dp,0.dp,10.dp,0.dp),
                            imageVector = Icons.Filled.FiberManualRecord,
                            contentDescription = null,
                            tint=it.color)
                        Text(text = it.name)
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f))
                        Text(text = if (it.currency) "Rp. ${DecimalFormat("#,###.00").format(items.value[i].y)}" else items.value[i].y.toString())
                    }
                }
            }
        }
    }

    data class Legend(
        val icon: ImageVector,
        val color: Color,
        val name: String
    )

    data class DataLabel(
        val color: Color,
        val name: String,
        val currency: Boolean
    )

    data class DataItem(
        val data: List<DataPoint>,
        val color: ChartColor,
        val title: String,
        val xTitle: String,
        val yTitle: String
    )

    private fun setMaxMin(dataIn: Pair<Long,Long>) {
        maxXData.value = dataIn.first
        minXData.value = dataIn.second
    }

    private fun getDataItem(sortedData: List<ItemHistory>,vararg types: TypeStatus): List<List<DataPoint>> {
        setMaxMin(getMaxAndMinItemDate(sortedData))
        val arr = mutableListOf<List<DataPoint>>()
        arr.add(mutableListOf())
        arr.add(mutableListOf())
        arr.add(mutableListOf())
        arr.add(mutableListOf())
        for (data in sortedData)
            for (type in types)
                when(type) {
                    TypeStatus.COST -> arr[0].plus(DataPoint(convertDateToDayOfYear(data.date.time,minXData.value),data.sellCost.toFloat()))
                    TypeStatus.SOLD -> arr[1].plus(DataPoint(convertDateToDayOfYear(data.date.time,minXData.value),data.sold_out.toFloat()))
                    TypeStatus.TOTAL -> arr[2].plus(DataPoint(convertDateToDayOfYear(data.date.time,minXData.value),data.total.toFloat()))
                    TypeStatus.STOCK -> arr[3].plus(DataPoint(convertDateToDayOfYear(data.date.time,minXData.value),data.stock.toFloat()))
                }
        return arr.filter { it.isNotEmpty() }
    }
}