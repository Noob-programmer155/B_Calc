package com.amrtm.android.bcalc

import android.annotation.SuppressLint
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amrtm.android.bcalc.component.data.Navigation
import com.amrtm.android.bcalc.component.data.viewmodel.HomeViewModel
import com.amrtm.android.bcalc.component.data.viewmodel.ItemViewModel
import com.amrtm.android.bcalc.component.data.viewmodel.NoteViewModel
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
                    Main(windowSize = windowSize, visual = visual)
                }
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun Main(
    windowSize: WindowSizeClass,
    navController: NavHostController = rememberNavController(),
    globalPadding: Dp = 0.dp,
    height: Dp = 100.dp,
    visual: Visualization
) {
    val focus = LocalFocusManager.current
    val homeModel: HomeViewModel = viewModel(factory = ViewMain.Factory)
    val itemModel: ItemViewModel = viewModel(factory = ViewMain.Factory)
    val noteModel: NoteViewModel = viewModel(factory = ViewMain.Factory)
    val drawerState: ScaffoldState = rememberScaffoldState()
    val thread: CoroutineScope = rememberCoroutineScope()
    val isNote: MutableState<Int> = remember { mutableStateOf(0) }
    val stateNavigation: MutableState<Int> = remember { mutableStateOf(2) }
    val searchState: MutableState<Boolean> = remember { mutableStateOf(false) }
    AppBarMain(
        navigationController = navController,
        windowWidth = windowSize.widthSizeClass,
        isNote = isNote,
        scaffoldState = drawerState,
        thread = thread,
        focus = focus,
        searchBtnTrigger = searchState,
        noteView = noteModel,
        itemView = itemModel,
        state = stateNavigation
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
                if (stateNavigation.value != 2)
                    stateNavigation.value = 2
                Home(
                    padding = globalPadding,
                    numberPage = 2
                ) {
                    when (type) {
                        "note" -> {
                            noteModel.clearQuery()
                            if (isNote.value != NOTE)
                                isNote.value = NOTE
                            Column {
                                CardAddLayout(
                                    navController = navController,
                                    height = height,
                                    modifier = Modifier
                                        .width(LocalConfiguration.current.screenWidthDp.dp)
                                        .fillMaxHeight(),
                                    storage = noteModel
                                )
                            }
                        }
                        "item" -> {
                            itemModel.clearQuery()
                            if (isNote.value != 2)
                                isNote.value = 2
                            Column {
                                ItemsList(
                                    windowSize = windowSize.widthSizeClass,
                                    navController = navController,
                                    modifier = Modifier
                                        .width(LocalConfiguration.current.screenWidthDp.dp)
                                        .fillMaxHeight(),
                                    storage = itemModel
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
                                    viewModel = homeModel,
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
                if (stateNavigation.value != 1)
                    stateNavigation.value = 1
                Column {
                    AddNote(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .weight(1f, false),
                        navController = navController,
                        focus = focus,
                        width = windowSize.widthSizeClass,
                    )
                }
            }
            composable(route = "${Navigation.VisualizeItem(null).link}?name={name}", arguments = listOf(
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
                if (stateNavigation.value != 0)
                    stateNavigation.value = 0
                visual.CreateVisualization(
                    name = it.arguments?.getString("name"),
                    navController = navController,
                    width = windowSize.widthSizeClass
                )
            }
        }
    }
}