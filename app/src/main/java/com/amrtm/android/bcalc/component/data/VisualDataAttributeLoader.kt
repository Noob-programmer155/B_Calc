package com.amrtm.android.bcalc.component.data

import androidx.compose.ui.graphics.Color
import co.yml.charts.common.model.Point
import com.amrtm.android.bcalc.component.view.Visualization
import java.util.*

object VisualDataAttributeLoader {
    fun bindDataPointToColor(
        dataPoint: List<Point>,
        title: String,
        xLabel: String,
        yLabel: String,
        date: List<Date>,
        type: Visualization.Companion.TypeStatus,
        labels: Visualization.Companion.DataLabel,
        legends: Visualization.Companion.Legend
    ): Pair<Visualization.Companion.TypeStatus,Visualization.Companion.DataItem> {
        return type to Visualization.Companion.DataItem(
            data = dataPoint,
            color = defaultColor()[type.index],
            xTitle = xLabel,
            yTitle = yLabel,
            title = title,
            dataPopup = labels,
            dataLegends = legends,
            date = date
        )
    }

    fun defaultColor(): List<ChartColor> {
        return listOf(
            ChartColor(
                mainColor = Color(0xFF5B8FB9),
                areaColor = Color(0xFF5B8FB9),
                interceptionColor = Color(0xFFBAD7E9),
                pointColor1 = Color(0xFF85CDFD),
                pointColor2 = Color.White
            ),
            ChartColor(
                mainColor = Color(0xFFFC7300),
                areaColor = Color(0xFFFC7300),
                interceptionColor = Color(0xFFF2CD5C),
                pointColor1 = Color(0xFFFEC868),
                pointColor2 = Color.White
            ),
            ChartColor(
                mainColor = Color(0xFF1F8A70),
                areaColor = Color(0xFF1F8A70),
                interceptionColor = Color(0xFFAACB73),
                pointColor1 = Color(0xFFABC270),
                pointColor2 = Color.White
            ),
            ChartColor(
                mainColor = Color(0xFF7B2869),
                areaColor = Color(0xFF58287F),
                interceptionColor = Color(0xFFC85C8E),
                pointColor1 = Color(0xFFB08BBB),
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