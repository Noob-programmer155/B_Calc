package com.amrtm.android.bcalc.component.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Square
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import co.yml.charts.common.model.Point
import com.amrtm.android.bcalc.ViewMain
import com.amrtm.android.bcalc.component.data.ChartColor
import com.amrtm.android.bcalc.component.data.VisualDataAttributeLoader
import com.amrtm.android.bcalc.component.data.repository.ItemHistory
import com.amrtm.android.bcalc.component.data.viewmodel.ItemViewModel
import com.amrtm.android.bcalc.ui.theme.BCalcTheme
import java.math.BigDecimal
import java.util.*
import kotlin.random.Random

class Visualization {
    companion object {
        enum class TypeStatus(val index:Int) {
            ALL(-1),
            COST(0),
            SOLD(1),
            STOCK(2),
            TOTAL(3)
        }

        data class Legend(
            val icon: ImageVector,
            val color: Color,
            val name: String
        )

        data class DataLabel(
//            val color: Color,
            val name: String,
            val currency: Boolean
        )

        data class DataItem(
            val data: List<Point>,
            val date: List<Date>,
            val color: ChartColor,
            val title: String,
            val xTitle: String,
            val yTitle: String,
            val dataPopup: DataLabel,
            val dataLegends: Legend
        )

        fun getDataItem(sortedData: ItemHistory,vararg types: TypeStatus,index: Int): Pair<Date,Map<TypeStatus,Point>> {
            val arr = mutableMapOf<TypeStatus,Point>()
            for (type in types) {
                when(type) {
                    TypeStatus.COST -> arr[TypeStatus.COST] = Point(index.toFloat(),sortedData.sellCost.toFloat())
                    TypeStatus.SOLD -> arr[TypeStatus.SOLD] = Point(index.toFloat(),sortedData.sold_out.toFloat())
                    TypeStatus.TOTAL -> arr[TypeStatus.TOTAL] = Point(index.toFloat(),sortedData.total.toFloat())
                    TypeStatus.STOCK -> arr[TypeStatus.STOCK] = Point(index.toFloat(),sortedData.stock.toFloat())
                    else -> {}
                }
            }
            return Pair(sortedData.date,arr)
        }
    }

    object Load {
        fun dataTest(): Map<TypeStatus,DataItem> {
            val randomCost = Random(10000)
            val randomDiscount = Random(10000)
            val randomPurchase = Random(10000)
            val randomStock = Random(10000)
            val day = 86400000
            val items = List(listOf(1..4).size) { i ->
                val cost = randomCost.nextLong(1000,100000)
                val discount = randomDiscount.nextInt(0,100)
                val purchase = randomPurchase.nextInt(40,400)
                val stock = randomStock.nextInt(0,400)
                ItemHistory(i.toLong()+1,
                    2,
                    "ITEM ${i}",
                    BigDecimal.valueOf(cost),
                    BigDecimal.valueOf(cost + 1000),
                    discount,
                    purchase - 38,
                    purchase,
                    stock,
                    stock + purchase - 38,
                    BigDecimal.valueOf((cost+1000) * discount / 100 * (purchase - 38)),
                    Date(Date().time - (day * (i-1))))
            }.mapIndexed {i,it ->
                getDataItem(it,*type.toTypedArray(), index = i+1)
            }
            return type.mapIndexed {i,type ->
                val item = items.map { it.second[type]!! }
                VisualDataAttributeLoader.bindDataPointToColor(
                    dataPoint = item,
                    title = title[i],
                    xLabel = xTitle[i],
                    yLabel = yTitle[i],
                    type = type,
                    labels = label[i],
                    legends = legends[i],
                    date = items.map { it.first }
                )
            }.toMap()
        }
        val type = listOf(TypeStatus.COST,TypeStatus.SOLD,TypeStatus.STOCK,TypeStatus.TOTAL)
        val title = listOf("Cost Item","Sold Item","Stock Item","Total Income")
        val xTitle = listOf("Date History","Date History","Date History","Date History")
        val yTitle = listOf("Cost","Count","In Stock","Total")
        val label = listOf(
            DataLabel(/*Color(0xFFFC7300),*/"Cost",true),
            DataLabel(/*Color.Green,*/"Count Sold",false),
            DataLabel(/*Color.Blue,*/"In Stock",false),
            DataLabel(/*Color.Cyan,*/"Total Income",true),
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
        }
        if(name?.isBlank() != false) {
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
                        style = MaterialTheme.typography.caption.copy(fontSize = 12.sp),
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
        state: MutableState<TypeStatus> = remember { mutableStateOf(TypeStatus.COST) }
    ) {
        val items = view.dataVisual.collectAsState(initial = listOf()).value.mapIndexed {i,it ->
            getDataItem(it,*Load.type.toTypedArray(), index = i)
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
                        .height(50.dp)
                        .padding(0.dp),
                    contentPadding = PaddingValues(10.dp,0.dp),
                    rows = GridCells.Adaptive(40.dp)
                ) {
//                    item {
//                        OutlinedButton(
//                            modifier = Modifier
//                                .padding(4.dp, 5.dp)
//                                .padding(0.dp),
//                            shape = RoundedCornerShape(50),
//                            onClick = { state.value = TypeStatus.ALL }
//                        ) {
//                            Text(text = "all")
//                        }
//                    }
                    items(typesButton) {
                        val background by animateColorAsState(targetValue = if(state.value == it) MaterialTheme.colors.secondary else Color.Transparent)
                        val content by animateColorAsState(targetValue = if(state.value == it) Color.White else MaterialTheme.colors.secondary)
                        OutlinedButton(
                            modifier = Modifier
                                .padding(4.dp, 5.dp)
                                .padding(0.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = background,
                                contentColor = content
                            ),
                            onClick = { state.value = it }
                        ) {
                            Text(text = it.name.lowercase())
                        }
                    }
                }
                if (items.isNotEmpty())
                    CreateVisualization(
                        data = Load.type.mapIndexed {i,type ->
                                val item = items.map { it.second[type]!! }
                                VisualDataAttributeLoader.bindDataPointToColor(
                                    dataPoint = item,
                                    title = Load.title[i],
                                    xLabel = Load.xTitle[i],
                                    yLabel = Load.yTitle[i],
                                    type = type,
                                    labels = Load.label[i],
                                    legends = Load.legends[i],
                                    date = items.map { it.first }
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
                if (data.isNotEmpty())
                    Graph.LineGraph(
                        state = state,
                        data = data
                    )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = if (state.value != TypeStatus.ALL) data[state.value]?.xTitle!! else defaultxLabel,
                maxLines = 3, style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center
            )
        }
        Box(modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth()
            .padding(10.dp, 0.dp, 10.dp, 20.dp)) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            ) {
                for(dt in if (state.value != TypeStatus.ALL) listOf(data[state.value]?.dataLegends!!) else data.map { it.value.dataLegends })
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = dt.icon, contentDescription = null, tint = dt.color)
                        Text(text = dt.name)
                    }
            }
        }
    }

//    @Composable
//    private fun PopUpLabel(
//        title: String,
//        cardWidth: MutableState<Int>,
//        cardHeight: MutableState<Dp>,
//        xOffset: MutableState<Float>,
//        dataLabel: List<DataLabel>,
//        items: MutableState<List<Point>>,
//        density: Density
//    ) {
//        Surface(
//            modifier = Modifier
//                .width(200.dp)
//                .onGloballyPositioned {
//                    cardWidth.value = it.size.width
//                    cardHeight.value = toDp(it.size.height, density).dp
//                }
//                .graphicsLayer(translationX = xOffset.value),
//            shape = RoundedCornerShape(12.dp),
//            color = Color(0xFFEAE0DA)
//        ) {
//            LazyColumn(
//                modifier = Modifier
//                    .padding(10.dp)
//                    .fillMaxWidth()
//            ) {
//                item {
//                    Text(text = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(convertDayOfYearToDate(items.value[0].x)), textAlign = TextAlign.End, style = MaterialTheme.typography.caption)
//                    Text(text = title, textAlign = TextAlign.Start, style = MaterialTheme.typography.h4)
//                    Divider(modifier = Modifier.padding(15.dp,0.dp))
//                }
//                itemsIndexed(dataLabel) {i,it ->
//                    Row {
//                        Icon(
//                            modifier = Modifier.padding(0.dp,0.dp,10.dp,0.dp),
//                            imageVector = Icons.Filled.FiberManualRecord,
//                            contentDescription = null,
//                            tint=it.color)
//                        Text(text = it.name)
//                        Spacer(modifier = Modifier
//                            .fillMaxWidth()
//                            .weight(1f))
//                        Text(text = if (it.currency) "Rp. ${DecimalFormat("#,###.00").format(items.value[i].y)}" else items.value[i].y.toString())
//                    }
//                }
//            }
//        }
//    }

    @Preview(widthDp = 480)
    @Composable
    fun ExampleLineGraph() {
        val state: MutableState<TypeStatus> = remember { mutableStateOf(TypeStatus.ALL) }
        BCalcTheme {
            Surface {
                Card(
                    modifier = Modifier
                        .padding(10.dp)
                        .padding(0.dp)
                ) {
                    Graph.LineGraph(
                        state = state,
                        data = Load.dataTest()
                    )
                }
            }
        }
    }
}