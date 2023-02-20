package com.amrtm.android.bcalc.component.view

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.extensions.formatToSinglePrecision
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object Graph {
    @Composable
    fun LineGraph(
        state: MutableState<Visualization.Companion.TypeStatus>,
        data: Map<Visualization.Companion.TypeStatus, Visualization.Companion.DataItem>,
    ) {
        val index = remember{ mutableStateOf(0) }
        LineChart(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxWidth()
                .height(250.dp),
            lineChartData = generateData(
                state = state,
                data = data,
                labelColor = MaterialTheme.colors.onBackground,
                index = index
            )
        )
    }
    private fun xAxis(steps: Int, dataPoints: List<Point>): AxisData {
        return AxisData.Builder()
            .axisStepSize(50.dp)
            .steps(steps)
            .labelData { i ->
                val xMin = dataPoints.minOf { it.x }
                val xMax = dataPoints.maxOf { it.x }
                val xScale = (xMax - xMin) / steps
                ((i * xScale)).formatToSinglePrecision()
            }
            .labelAndAxisLinePadding(15.dp)
            .build()
    }

    private fun yAxis(steps: Int, dataPoints: List<Point>): AxisData {
        return AxisData.Builder()
            .steps(steps)
            .labelAndAxisLinePadding(15.dp)
            .labelData { i ->
                val yMin = dataPoints.minOf { it.y }
                val yMax = dataPoints.maxOf { it.y }
                val yScale = (yMax - yMin) / steps
                ((i * yScale) + yMin).formatToSinglePrecision()
            }.build()
    }

    private fun generateData(
        state: MutableState<Visualization.Companion.TypeStatus>,
        data: Map<Visualization.Companion.TypeStatus, Visualization.Companion.DataItem>,
        labelColor: Color,
        index: MutableState<Int>
    ): LineChartData {
        val points = data[state.value]
        return LineChartData(
            xAxisData = xAxis(
                steps = points?.data?.size!!/4,
                dataPoints = if (state.value != Visualization.Companion.TypeStatus.ALL) points.data
                    else listOf(*data[Visualization.Companion.TypeStatus.COST]?.data?.toTypedArray()!!,*data[Visualization.Companion.TypeStatus.TOTAL]?.data?.toTypedArray()!!)
            ),
            yAxisData = yAxis(
                steps = 9,
                dataPoints = if (state.value != Visualization.Companion.TypeStatus.ALL) points.data
                    else listOf(*data[Visualization.Companion.TypeStatus.COST]?.data?.toTypedArray()!!,*data[Visualization.Companion.TypeStatus.TOTAL]?.data?.toTypedArray()!!)
            ),
            linePlotData = LinePlotData(
                plotType = PlotType.Line,
                lines = if (state.value != Visualization.Companion.TypeStatus.ALL) {
                    listOf(
                        Line(
                            dataPoints = data[state.value]?.data!!,
                            lineStyle = LineStyle(
                                lineType = LineType.SmoothCurve(),
                                color = data[state.value]?.color?.mainColor!!
                            ),
                            intersectionPoint = IntersectionPoint(color = data[state.value]?.color?.interceptionColor!!),
                            selectionHighlightPoint = SelectionHighlightPoint(
                                color = data[state.value]?.color?.pointColor1!!
                            ),
                            shadowUnderLine = ShadowUnderLine(color = data[state.value]?.color?.areaColor!!, alpha = .4f),
                            selectionHighlightPopUp = SelectionHighlightPopUp(
//                                backgroundColor = backgroundLabel,
                                backgroundCornerRadius = CornerRadius(5f,5f),
                                labelColor = labelColor,
                                labelAlignment = if(index.value <= 1) Paint.Align.LEFT
                                    else if(index.value >= data[state.value]?.data?.size!!-1) Paint.Align.RIGHT
                                        else Paint.Align.CENTER,
                                popUpLabel = {x,y ->
                                    index.value = x.toInt()
                                    val dt = data[state.value]
                                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(dt?.date?.get(x.toInt())!!)
                                    if (dt.dataPopup.currency) "$date | Rp ${DecimalFormat("#,###").format(y)}.00" else "$date | ${y}"
                                }
                            )
                        )
                    )
                }
                else
                    data.map {
                        Line(
                            dataPoints = it.value.data,
                            lineStyle = LineStyle(
                                lineType = LineType.SmoothCurve(),
                                color = it.value.color.mainColor,
                            ),
                            intersectionPoint = IntersectionPoint(color = it.value.color.interceptionColor),
                            selectionHighlightPoint = SelectionHighlightPoint(
                                color = it.value.color.pointColor1
                            ),
                            shadowUnderLine = ShadowUnderLine(color = it.value.color.areaColor, alpha = .4f),
                            selectionHighlightPopUp = SelectionHighlightPopUp(
//                                backgroundColor = backgroundLabel,
                                backgroundCornerRadius = CornerRadius(5f,5f),
                                labelColor = labelColor,
                                labelAlignment = if(index.value <= 1) Paint.Align.LEFT
                                else if(index.value >= data[state.value]?.data?.size!!-1) Paint.Align.RIGHT
                                else Paint.Align.CENTER,
                                popUpLabel = {x,y ->
                                    index.value = x.toInt()
                                    val dt = data[state.value]
                                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(dt?.date?.get(x.toInt())!!)
                                    if (dt.dataPopup.currency) "$date | Rp ${DecimalFormat("#,###").format(y)}.00" else "$date | ${y}"
                                }
                            )
                        )
                    },
            ),
            gridLines = GridLines()
        )
    }
}