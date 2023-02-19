package com.amrtm.android.bcalc.component.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import androidx.room.util.newStringBuilder
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
        LineChart(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxWidth()
                .height(250.dp),
            lineChartData = generateData(
                state = state,
                data = data,
                backgroundLabel = MaterialTheme.colors.background,
                labelColor = MaterialTheme.colors.onBackground
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
                ((i * xScale) + xMin).formatToSinglePrecision()
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
        backgroundLabel: Color,
        labelColor: Color,
        widthLine: Float = 3f
    ): LineChartData {
        return LineChartData(
            xAxisData = xAxis(
                steps = data.values.toList()[0].data.size/2,
                dataPoints = if (state.value != Visualization.Companion.TypeStatus.ALL) data[state.value]?.data!!
                else listOf(*data[Visualization.Companion.TypeStatus.COST]?.data?.toTypedArray()!!,*data[Visualization.Companion.TypeStatus.TOTAL]?.data?.toTypedArray()!!)
            ),
            yAxisData = yAxis(
                steps = 9,
                dataPoints = if (state.value != Visualization.Companion.TypeStatus.ALL) data[state.value]?.data!!
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
//                            selectionHighlightPopUp = SelectionHighlightPopUp(
//                                backgroundColor = backgroundLabel,
//                                backgroundCornerRadius = CornerRadius(10f,10f),
//                                labelColor = labelColor,
//                                popUpLabel = {x,y ->
//                                    val dt = data[state.value]!!
//                                    val index = dt.data.indexOf(Point(x,y))
//                                    val str = newStringBuilder()
//                                    str.append(dt.title).append("\n")
//                                        .append(SimpleDateFormat("dd/MM/yyyy", Locale.US).format(dt.date[index])).append("\n")
//                                        .append("-----------------------------------").append("\n")
//                                    for (hy in dt.dataPopup) {
//                                        str.append(hy.name).append(" : ")
//                                        if (hy.currency)
//                                            str.append("Rp. ${DecimalFormat("#,###.00").format(y)}\n")
//                                        else
//                                            str.append(y.toString())
//                                    }
//                                    str.toString()
//                                }
//                            )
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
//                            selectionHighlightPopUp = SelectionHighlightPopUp(
//                                backgroundColor = backgroundLabel,
//                                backgroundCornerRadius = CornerRadius(10f,10f),
//                                labelColor = labelColor,
//                                popUpLabel = {x,y ->
//                                    val dt = it.value
//                                    val index = dt.data.indexOf(Point(x,y))
//                                    val str = newStringBuilder()
//                                    str.append(dt.title).append("\n")
//                                        .append(SimpleDateFormat("dd/MM/yyyy", Locale.US).format(dt.date[index])).append("\n")
//                                        .append("-----------------------------------").append("\n")
//                                    for (hy in dt.dataPopup) {
//                                        str.append(hy.name).append(" : ")
//                                        if (hy.currency)
//                                            str.append("Rp. ${DecimalFormat("#,###.00").format(y)}\n")
//                                        else
//                                            str.append(y.toString())
//                                    }
//                                    str.toString()
//                                }
//                            )
                        )
                    },
            ),
            gridLines = GridLines()
        )
    }
}