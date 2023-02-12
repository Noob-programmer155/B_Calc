package com.amrtm.android.bcalc.component.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.amrtm.android.bcalc.R

sealed class Navigation(@StringRes val resId: Int, val link: String) {
    class Home : Navigation(R.string.home_navigation,"home")
    class HomeNote : Navigation(R.string.navigation_home_note, "${Home().link}?type=note")
    class HomeItem : Navigation(R.string.navigation_home_item, "${Home().link}?type=item")
    class Note(id: Long?) : Navigation(R.string.adding_note_calc_navigation,if(id == null) "note" else "note?id=${id}")
//    Visualize(resId = R.string.visualization_navigation, "visualize"),
    class VisualizeItem: Navigation(R.string.visualization_navigation,"visualize/item/{page}")
}

data class NavigationItem(
    val icon: ImageVector,
    val route: Navigation,
)

//data class AdditionalNavigationItem(
//    val label: String,
//    val link: String
//)

object NavigationLoader {
    fun DefaultItem(): List<NavigationItem> {
        return listOf(
            NavigationItem(Icons.Filled.StackedLineChart, Navigation.VisualizeItem()),
            NavigationItem(Icons.Filled.Edit, Navigation.Note(null)),
            NavigationItem(Icons.Default.Home, Navigation.Home()),
        )
    }

    fun DefaultHomeItem(): List<NavigationItem> {
        return listOf(
            NavigationItem(Icons.Default.AccountBalance, Navigation.Home()),
            NavigationItem(Icons.Filled.StickyNote2, Navigation.HomeNote()),
            NavigationItem(Icons.Filled.Ballot, Navigation.HomeItem()),
        )
    }
//    fun VisualisationItem(): List<NavigationItem> {
//        return listOf(
//            NavigationItem(Icons.Default.Home, Navigation.Home),
//            NavigationItem(Icons.Filled.Category, Navigation.VisualizeItem),
//        )
//    }
}
