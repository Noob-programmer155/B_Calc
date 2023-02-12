package com.amrtm.android.bcalc.component.data

import androidx.compose.ui.graphics.Color
import com.amrtm.android.bcalc.component.view.Visualization
import com.madrapps.plot.line.DataPoint

object VisualDataAttributeLoader {
    fun bindDataPointsToColor(
        datapoints: List<List<DataPoint>>,
        titles: List<String>,
        xLabel: List<String>,
        yLabel: List<String>,
        vararg type: Visualization.TypeStatus) : Map<Visualization.TypeStatus,Visualization.DataItem> {
        if (datapoints.size != type.size && titles.size != type.size && xLabel.size != type.size && yLabel.size != type.size)
            throw Exception("size must same in both list")
        val map = mutableMapOf<Visualization.TypeStatus,Visualization.DataItem>()
        datapoints.forEachIndexed {i,it ->
            val color = defaultColor().get(i % 4)
            map.put(type[i],Visualization.DataItem(data = it, color = color,titles[i], xTitle = xLabel[i], yTitle = yLabel[i]))
        }
        return map
    }

    fun defaultColor(): List<ChartColor> {
        return listOf<ChartColor>(
            ChartColor(
                mainColor = Color(0xFF5B8FB9),
                areaColor = Color(0xFF5B8FB9).copy(alpha = 0.2f),
                interceptionColor = Color(0xFF7286D3),
                pointColor1 = Color(0xFF8EA7E9).copy(alpha = 0.5f),
                pointColor2 = Color.White
            ),
            ChartColor(
                mainColor = Color(0xFFFC7300),
                areaColor = Color(0xFFFC7300).copy(alpha = 0.2f),
                interceptionColor = Color(0xFFFF8B13),
                pointColor1 = Color(0xFFFF6E31).copy(alpha = 0.5f),
                pointColor2 = Color.White
            ),
            ChartColor(
                mainColor = Color(0xFF1F8A70),
                areaColor = Color(0xFF1F8A70).copy(alpha = 0.2f),
                interceptionColor = Color(0xFFAACB73),
                pointColor1 = Color(0xFFABC270).copy(alpha = 0.5f),
                pointColor2 = Color.White
            ),
            ChartColor(
                mainColor = Color(0xFFFFE15D),
                areaColor = Color(0xFFFFE15D).copy(alpha = 0.2f),
                interceptionColor = Color(0xFFF0FF42),
                pointColor1 = Color(0xFFF5EA5A).copy(alpha = 0.5f),
                pointColor2 = Color.White
            ),
        )
    }
}

data class ChartColor(
    val mainColor: Color,
    val areaColor: Color,
    val interceptionColor: Color,
    val pointColor1: Color,
    val pointColor2: Color
)