package com.amrtm.android.bcalc

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amrtm.android.bcalc.component.data.Navigation
import com.amrtm.android.bcalc.component.data.viewmodel.HomeViewModel
import com.amrtm.android.bcalc.component.view.*
import com.amrtm.android.bcalc.ui.theme.BCalcTheme
import kotlinx.coroutines.CoroutineScope

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BCalcTheme {
                // A surface containcom.amrtm.android.bcalc.component.er using the 'background' color from the theme
                val windowSize = calculateWindowSizeClass(activity = this)
                val visual = Visualization()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.surface
                ) {
                    Main(windowSize = windowSize, context = this, visual = visual)
                }
            }
        }
    }
}

@Composable
fun Main(
    windowSize: WindowSizeClass,
    navController: NavHostController = rememberNavController(),
    globalPadding: Dp = 0.dp,
    height: Dp = 100.dp,
    visual: Visualization,
    context: Context
) {
    val focus = LocalFocusManager.current
    val viewModel: HomeViewModel = viewModel(factory = ViewMain.Factory)
    val drawerState: ScaffoldState = rememberScaffoldState()
    val thread: CoroutineScope = rememberCoroutineScope()
    val isNote: MutableState<Int> = remember { mutableStateOf(0) }
    val searchState: MutableState<Boolean> = remember { mutableStateOf(false) }
    AppBarMain(
        navigationController = navController,
        windowWidth = windowSize.widthSizeClass,
        isNote = isNote,
        scaffoldState = drawerState,
        thread = thread,
        focus = focus,
        searchBtnTrigger = searchState
    ) {
        NavHost(
            navController = navController,
            startDestination = "${Navigation.Home().link}?type={type}&page={page}"
        ) {
            composable(route = "${Navigation.Home().link}?type={type}&page={page}", arguments = listOf(
                navArgument("type") {
                    nullable = true},
                navArgument("page") {
                    defaultValue = if (windowSize.widthSizeClass == WindowWidthSizeClass.Compact) 10 else if (windowSize.widthSizeClass == WindowWidthSizeClass.Medium) 20 else 30
                }
            )) {
                val type = it.arguments?.getString("type")
                Home(
                    padding = globalPadding,
                    numberPage = 2
                ) {
                    when (type) {
                        "note" -> {
                            if (isNote.value != NOTE)
                                isNote.value = NOTE
                            Column {
                                CardAddLayout(
                                    navController = navController,
                                    height = height,
                                    modifier = Modifier
                                        .width(LocalConfiguration.current.screenWidthDp.dp)
                                        .fillMaxHeight()
                                )
                            }
                        }
                        "item" -> {
                            if (isNote.value != 2)
                                isNote.value = 2
                            Column {
                                ItemsList(
                                    windowSize = windowSize.widthSizeClass,
                                    navController = navController,
                                    modifier = Modifier
                                        .width(LocalConfiguration.current.screenWidthDp.dp)
                                        .fillMaxHeight()
                                )
                            }
                        }
                        else -> {
                            if (isNote.value != 0)
                                isNote.value = 0
                            if (searchState.value)
                                searchState.value = false
                            Column {
                                BalanceCard(
                                    screenWidth = windowSize.widthSizeClass,
                                    viewModel = viewModel,
                                    modifier = Modifier
                                        .width(LocalConfiguration.current.screenWidthDp.dp)
//                                        .fillMaxHeight()
                                        .verticalScroll(rememberScrollState())
                                        .weight(1f, false)
                                )
                            }
                        }
                    }
                }
            }
            composable(route = "${Navigation.Note(null).link}?id={id}", arguments = listOf(
                navArgument("id") {
                    nullable = true
                }
            )) {
                if (isNote.value != 0)
                    isNote.value = 0
                if (searchState.value)
                    searchState.value = false
                Column {
                    AddNote(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .weight(1f, false),
                        navController = navController,
                        homeView = viewModel,
                        focus = focus
                    )
                }
            }
            composable(route = "${Navigation.VisualizeItem().link}?name={name}", arguments = listOf(
                navArgument("page") {
                    defaultValue = if (windowSize.widthSizeClass == WindowWidthSizeClass.Compact) 10 else if (windowSize.widthSizeClass == WindowWidthSizeClass.Medium) 20 else 30
                },
                navArgument("name") {
                    nullable = true
                }
            )) {
                if (isNote.value != 0)
                    isNote.value = 0
                if (searchState.value)
                    searchState.value = false
//                    visual.CreateVisualization(name = it.arguments?.getString("name"), navController = navController)
            }
        }
    }
}