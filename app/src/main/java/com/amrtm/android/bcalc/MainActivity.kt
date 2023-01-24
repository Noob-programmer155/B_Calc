package com.amrtm.android.bcalc

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amrtm.android.bcalc.component.data.Navigation
import com.amrtm.android.bcalc.component.data.repository.MainContainerImpl
import com.amrtm.android.bcalc.component.data.viewmodel.HomeViewModel
import com.amrtm.android.bcalc.component.view.*
import com.amrtm.android.bcalc.ui.theme.BCalcTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BCalcTheme {
                // A surface containcom.amrtm.android.bcalc.component.er using the 'background' color from the theme
                val windowSize = calculateWindowSizeClass(activity = this)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.surface
                ) {
                    Main(windowSize = windowSize.widthSizeClass, context = this)
                }
            }
        }
    }
}

@Composable
fun Main(
    windowSize: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController(),
    globalPadding: Dp = 30.dp,
    context: Context
) {
    val viewModel: HomeViewModel = viewModel(factory = ViewMain.Factory)
    val height: Dp = 400.dp
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        com.amrtm.android.bcalc.component.view.AppBar(
            navigationController = navController,
            isFullScreen = windowSize >= WindowWidthSizeClass.Medium,
        ) {
            NavHost(
                navController = navController,
                startDestination = Navigation.Home.link
            ) {
                composable(route = "${Navigation.Home.link}/{type}") {
                    val type = it.arguments?.get("type") as String?
                    Home(
                        navController = navController,
                        padding = globalPadding
                    ) {
                        when (type) {
                            "note" -> CardAddLayout(
                                navController = navController,
                                height = height,
                                windowSize = windowSize,
                                collapse = viewModel,
                                context = context
                            )
                            "item" -> ItemsList(
                                windowSize = windowSize,
                                navController = navController
                            )
                            else -> BalanceCard(
                                screenWidth = windowSize,
                                viewModel = viewModel
                            )
                        }
                    }
                }
                composable(route = "${Navigation.Note.link}/{id}") {
                    val id = it.arguments?.get("id") as Int?
                    AddNote(padding = globalPadding, id = id)
                }
                composable(route = "${Navigation.Visualize.link}/{type}/{all}/{id}") {
                    val type = it.arguments?.get("type")
                    val all = it.arguments?.get("all") == "items"
                    val id = it.arguments?.get("id") ?: -1

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val windowSize = calculateWindowSizeClass(activity = MainActivity())
    val context = MainActivity()
    BCalcTheme {
        Main(windowSize = windowSize.widthSizeClass, context = context)
    }
}