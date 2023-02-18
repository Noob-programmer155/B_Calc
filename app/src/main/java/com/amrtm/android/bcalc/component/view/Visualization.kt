package com.amrtm.android.bcalc.component.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.rounded.Square
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.amrtm.android.bcalc.ViewMain
import com.amrtm.android.bcalc.component.data.ChartColor
import com.amrtm.android.bcalc.component.data.VisualDataAttributeLoader
import com.amrtm.android.bcalc.component.data.repository.ItemHistory
import com.amrtm.android.bcalc.component.data.viewmodel.ItemViewModel
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class Visualization {
    companion object {
        enum class TypeStatus(val index:Int) {
            ALL(-1),
            COST(0),
            SOLD(1),
            STOCK(2),
            TOTAL(3)
        }

        val minXData: MutableState<Long> = mutableStateOf(Long.MAX_VALUE)
        val maxXData: MutableState<Long> = mutableStateOf(Long.MIN_VALUE)

        fun updateVisual() {
            minXData.value = Long.MAX_VALUE
            maxXData.value = Long.MIN_VALUE
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
            val yTitle: String,
            val dataPopup: List<DataLabel>,
            val dataLegends: List<Legend>
        )

        fun getDataItem(sortedData: ItemHistory,vararg types: TypeStatus): Map<TypeStatus,DataPoint> {
            if (sortedData.date.time > maxXData.value)
                maxXData.value = sortedData.date.time
            if (sortedData.date.time < minXData.value)
                minXData.value = sortedData.date.time
            val date = convertDateToDayOfYear(sortedData.date.time)
            val arr = mutableMapOf<TypeStatus,DataPoint>()
            for (type in types) {
                when(type) {
                    TypeStatus.COST -> arr.put(TypeStatus.COST,DataPoint(date,sortedData.sellCost.toFloat()))
                    TypeStatus.SOLD -> arr.put(TypeStatus.SOLD,DataPoint(date,sortedData.sold_out.toFloat()))
                    TypeStatus.TOTAL -> arr.put(TypeStatus.TOTAL,DataPoint(date,sortedData.total.toFloat()))
                    TypeStatus.STOCK -> arr.put(TypeStatus.STOCK,DataPoint(date,sortedData.stock.toFloat()))
                    else -> {}
                }
            }
            return arr
        }
    }

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
        view: ItemViewModel = viewModel(factory = ViewMain.Factory),
        navController: NavHostController,
        width: WindowWidthSizeClass
    ) {
        LaunchedEffect(key1 = Unit) {
            view.setName(name)
            updateVisual()
        }
        if(name?.isBlank() ?: true) {
            WelcomePage(
                view = view,
                modifier = Modifier,
                navController = navController,
                width = width
            )
        } else {
            Surface(
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 80.dp)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(0.dp)
            ) {
                CreateVisualizationCard(
                    typesButton = Load.type,
                    modifier = Modifier.padding(15.dp),
                    view = view
                )
            }
        }
    }

    @Composable
    private fun WelcomePage(
        view: ItemViewModel,
        navController: NavHostController,
        modifier: Modifier,
        width: WindowWidthSizeClass
    ) {
        val items = view.pagingData.collectAsLazyPagingItems()
//        val balance by homeView.balanceData.collectAsState()
        Column (
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .padding(0.dp)
                        .background(color = MaterialTheme.colors.secondary)
                        .padding(10.dp)
                ) {
                    Text(text = "Welcome To", style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSecondary)
                    Text(text = "Data Visualization", style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.onSecondary)
                    Text(
                        text = "In here you can see your data item history and make your future decision of your plan to grow your business",
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSecondary
                    )
                    Text(
                        text = "(You can see your item history data in category cost to see " +
                                "cost history, sold item history, stock item history, and total income in one item history)",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSecondary
                    )
                }
//                Card(
//                    modifier = Modifier
//                        .padding(10.dp)
//                        .padding(0.dp),
//                    backgroundColor = MaterialTheme.colors.secondaryVariant,
//                    contentColor = MaterialTheme.colors.onSecondary,
//                    shape = MaterialTheme.shapes.medium,
//                    elevation = 8.dp
//                ) {
//                    Column(
//                        modifier = Modifier.padding(15.dp)
//                    ) {
//                        Text(text = "Your Balance Status", style = MaterialTheme.typography.h3)
//                        Text(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            text = "Income:", style = MaterialTheme.typography.h6,
//                            textAlign = TextAlign.Start
//                        )
//                        Text(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            text = "Rp. ${DecimalFormat("#,###.00").format(balance.income ?: BigDecimal.ZERO)}",
//                            style = MaterialTheme.typography.h5,
//                            textAlign = TextAlign.End
//                        )
//                        Text(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            text = "Outcome:", style = MaterialTheme.typography.h6,
//                            textAlign = TextAlign.Start
//                        )
//                        Text(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            text = "Rp. ${DecimalFormat("#,###.00").format(balance.outcome ?: BigDecimal.ZERO)}",
//                            style = MaterialTheme.typography.h5,
//                            textAlign = TextAlign.End
//                        )
//                        Text(text = "Total Notes: ${balance.noteCount}", style = MaterialTheme.typography.body1)
//                        Text(text = "Total Items: ${balance.itemsCount}", style = MaterialTheme.typography.body1)
//                    }
//                }
//                Divider(modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(0.dp, 20.dp, 0.dp, 10.dp))
//                Row {
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
//                }
//                Divider(modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(0.dp, 10.dp, 0.dp, 20.dp))
                ItemsView(
                    items = items,
                    navController = navController,
                    width = width,
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 80.dp)
                        .padding(0.dp)
                )
            }
        }
    }

    @Composable
    private fun CreateVisualizationCard(
        typesButton: List<TypeStatus>,
        modifier: Modifier,
        view: ItemViewModel,
        state: MutableState<TypeStatus> = remember { mutableStateOf(TypeStatus.ALL) }
    ) {
        val items = view.dataVisual.collectAsState(initial = listOf()).value.map {
            getDataItem(it,*Load.type.toTypedArray())
        }
        Card(
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
            elevation = 15.dp
        ) {
            Column {
                LazyHorizontalGrid(
                    modifier = modifier
                        .padding(0.dp, 10.dp, 0.dp, 5.dp)
                        .height(120.dp)
                        .padding(0.dp),
                    contentPadding = PaddingValues(5.dp),
                    rows = GridCells.Adaptive(40.dp)
                ) {
                    item {
                        OutlinedButton(
                            modifier = Modifier
                                .padding(4.dp,5.dp)
                                .padding(0.dp),
                            shape = RoundedCornerShape(50),
                            onClick = { state.value = TypeStatus.ALL }
                        ) {
                            Text(text = "all")
                        }
                    }
                    items(typesButton) {
                        OutlinedButton(
                            modifier = Modifier
                                .padding(4.dp,5.dp)
                                .padding(0.dp),
                            shape = RoundedCornerShape(50),
                            onClick = { state.value = it }
                        ) {
                            Text(text = it.name.lowercase())
                        }
                    }
                }
                if (items.isNotEmpty())
                    CreateVisualization(
                        data = Load.type.mapIndexed {i,type ->
                                val item = items.map { it[type]!! }
                                VisualDataAttributeLoader.bindDataPointToColor(
                                    dataPoint = item,
                                    title = Load.title[i],
                                    xLabel = Load.xTitle[i],
                                    yLabel = Load.yTitle[i],
                                    type = type,
                                    labels = listOf(Load.label[i]),
                                    legends = listOf(Load.legends[i])
                                )
                            }.toMap(),
                        state = state,
                    )
            }
        }
    }

    @Composable
    private fun CreateVisualization (
        data: Map<TypeStatus,DataItem>,
        state: MutableState<TypeStatus>,
        defaultxLabel: String = "Date History"
    ) {
        val totalWidth = remember { mutableStateOf(0) }
        val xOffset = remember { mutableStateOf(0f) }
        val cardWidth = remember { mutableStateOf(0) }
        val cardHeight = remember { mutableStateOf(100.dp) }
        val visibility = remember { mutableStateOf(false) }
        val points = remember { mutableStateOf(listOf<DataPoint>()) }
        val density = LocalDensity.current
        val padding = 16.dp
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = if (state.value != TypeStatus.ALL) data[state.value]?.title!! else "All History",
                style = MaterialTheme.typography.h4,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.rotate(90f),
                    text = if (state.value != TypeStatus.ALL) data[state.value]?.yTitle!! else "Status",
                    maxLines = 3, style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center
                )
                if(data.values.size > 0)
                    if (data.values.toList().first().data.size > 0)
                    LineGraph(
                        state = state,
                        data = data,
                        visibility = visibility,
                        cardWidth = cardWidth,
                        padding = padding,
                        density = density,
                        totalWidth = totalWidth,
                        xOffset = xOffset,
                        points = points
                    )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = if (state.value != TypeStatus.ALL) data[state.value]?.xTitle!! else defaultxLabel,
                maxLines = 3, style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center
            )
        }
        Box(modifier = Modifier.height(cardHeight.value)) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(100.dp),
                contentPadding = PaddingValues(10.dp),
                content = {
                    itemsIndexed(if (state.value != TypeStatus.ALL) data[state.value]?.dataLegends!! else data.map { it.value.dataLegends.first() }) {_,it ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = it.icon, contentDescription = null, tint = it.color)
                            Text(text = it.name)
                        }
                    }
                }
            )
            if (visibility.value) {
                if (points.value.size > 0)
                    PopUpLabel(
                        title = if (state.value != TypeStatus.ALL) data[state.value]?.title!! else "Item History",
                        cardHeight = cardHeight,
                        cardWidth = cardWidth,
                        xOffset = xOffset,
                        dataLabel = if (state.value != TypeStatus.ALL) data[state.value]?.dataPopup!! else data.map { it.value.dataPopup.first() },
                        items = points,
                        density = density
                    )
            }
        }
    }

    @Composable
    fun LineGraph(
        state: MutableState<TypeStatus>,
        data: Map<TypeStatus,DataItem>,
        visibility: MutableState<Boolean>,
        cardWidth: MutableState<Int>,
        padding: Dp,
        density: Density,
        totalWidth: MutableState<Int>,
        xOffset: MutableState<Float>,
        points: MutableState<List<DataPoint>>
    ) {
        LineGraph(
            plot = LinePlot(
                if (state.value != TypeStatus.ALL) {
                    Log.i("ITEMS",data[state.value]?.data!!.joinToString{ "points: (${it.x},${it.y})" })
                    listOf(
                        LinePlot.Line(
                            data[state.value]?.data!!.map { it.copy(x = it.x - minXData.value) },
                            LinePlot.Connection(data[state.value]?.color?.mainColor!!),
                            LinePlot.Intersection(data[state.value]?.color?.interceptionColor!!),
                            LinePlot.Highlight { center ->
                                val color = data[state.value]?.color?.pointColor1!!
                                drawCircle(color, 9.dp.toPx(), center, alpha = 0.3f)
                                drawCircle(color, 6.dp.toPx(), center)
                                drawCircle(data[state.value]?.color?.pointColor2!!, 3.dp.toPx(), center)
                            },
                            LinePlot.AreaUnderLine(data[state.value]?.color?.areaColor!!)
                        )
                    )
                }
                else
                    data.map {
                        Log.i("ITEMS",it.value.data.joinToString{ "points: (${it.x},${it.y})" })
                        LinePlot.Line(
                            it.value.data.map {items -> items.copy(x = items.x - minXData.value) },
                            LinePlot.Connection(it.value.color.mainColor),
                            LinePlot.Intersection(it.value.color.interceptionColor),
                            LinePlot.Highlight { center ->
                                val color = it.value.color.pointColor1
                                drawCircle(color, 9.dp.toPx(), center, alpha = 0.3f)
                                drawCircle(color, 6.dp.toPx(), center)
                                drawCircle(it.value.color.pointColor2, 3.dp.toPx(), center)
                            },
                            LinePlot.AreaUnderLine(it.value.color.areaColor)
                        )
                    }
            ),
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxWidth()
                .height(220.dp),
            onSelectionStart = { visibility.value = true },
            onSelectionEnd = { visibility.value = false }
        )
        {x, pts ->
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
                    Text(text = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(convertDayOfYearToDate(items.value[0].x)), textAlign = TextAlign.End, style = MaterialTheme.typography.caption)
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

    fun dataExample(): Map<TypeStatus, DataItem> = mapOf(
//        Pair(TypeStatus.TOTAL,DataItem())
    )

    @Preview(widthDp = 480)
    @Composable
    fun ExampleLineGraph() {
        val state: MutableState<TypeStatus> = remember { mutableStateOf(TypeStatus.ALL) }
        val totalWidth = remember { mutableStateOf(0) }
        val xOffset = remember { mutableStateOf(0f) }
        val cardWidth = remember { mutableStateOf(0) }
        val visibility = remember { mutableStateOf(false) }
        val points = remember { mutableStateOf(listOf<DataPoint>()) }
        val density = LocalDensity.current
        val padding = 16.dp
        Surface {
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .padding(0.dp)
            ) {
                LineGraph(
                    state = state,
                    data = dataExample(),
                    visibility = visibility,
                    cardWidth = cardWidth,
                    padding = padding,
                    density = density,
                    totalWidth = totalWidth,
                    xOffset = xOffset,
                    points = points
                )
            }
        }
    }
}