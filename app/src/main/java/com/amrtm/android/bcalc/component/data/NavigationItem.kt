package com.amrtm.android.bcalc.component.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.amrtm.android.bcalc.R

enum class Navigation(@StringRes val resId: Int, val link: String) {
    Home(resId = R.string.home_navigation,"home"),
    Note(resId = R.string.adding_note_calc_navigation,"note"),
    Visualize(resId = R.string.visualization_navigation, "visualize"),
    VisualizeItem(resId = R.string.visualization_navigation_item, "visualize/item"),
    VisualizeNote(resId = R.string.visualization_navigation_note, "visualize/note")
}

data class NavigationItem(
    val icon: ImageVector,
    val name: Navigation
)

class DataLoader () {
    fun DefaultItem(): List<NavigationItem> {
        return listOf<NavigationItem>(
            NavigationItem(Icons.Default.Home, Navigation.Home),
            NavigationItem(Icons.Default.Build, Navigation.Note),
            NavigationItem(Icons.Default.Star, Navigation.Visualize),
        )
    }
}
