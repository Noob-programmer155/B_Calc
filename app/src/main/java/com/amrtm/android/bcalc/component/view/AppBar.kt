package com.amrtm.android.bcalc.component.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.amrtm.android.bcalc.R
import com.amrtm.android.bcalc.component.data.NavigationItem
import com.amrtm.android.bcalc.component.data.NavigationLoader
import com.amrtm.android.bcalc.component.data.viewmodel.ItemViewModel
import com.amrtm.android.bcalc.component.data.viewmodel.NoteViewModel
import com.amrtm.android.bcalc.ui.theme.BCalcTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppBarMain(
//    openState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
//    thread: CoroutineScope = rememberCoroutineScope(),
    navigationController: NavHostController,
    windowWidth: WindowWidthSizeClass,
    painter: Painter? = painterResource(id = R.drawable.iconbcalc_1),
    collapseItemHome: MutableState<Boolean> = remember { mutableStateOf(false) },
    noteView: NoteViewModel,
    itemView: ItemViewModel,
    isNote: MutableState<Int>,
    searchBtnTrigger: MutableState<Boolean>,
    scaffoldState: ScaffoldState,
    thread: CoroutineScope,
    focus: FocusManager,
    content: @Composable PaddingValues.() -> Unit
) {
    Scaffold(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            if (collapseItemHome.value)
                collapseItemHome.value = false
            focus.clearFocus()},
        topBar = {
            TopBar(
                painter = painter,
                noteView = noteView,
                itemView = itemView,
                isNote = isNote,
                scaffoldState = scaffoldState,
                focus = focus,
                thread = thread,
                searchBtnTrigger = searchBtnTrigger
            )},
        content = content,
        bottomBar = {
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                CollapseItemHome(
                    navController = navigationController,
                    open = collapseItemHome,
                    windowWidth = windowWidth
                )
                Navigation(
                    navController = navigationController,
                    collapseItemHome = collapseItemHome,
                    windowWidth = windowWidth
                )
            }
        },
        scaffoldState = scaffoldState,
        drawerShape = RectangleShape,
        drawerElevation = 15.dp,
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            FilterMain(
                noteView = noteView,
                itemView = itemView,
                scaffoldState = scaffoldState,
                isNote = isNote.value == NOTE,
                thread = thread,
                focus = focus
            )
        }
    )
}

@Composable
private fun TopBar(
//    openState: DrawerState,
//    thread: CoroutineScope,
//    borderStrokeColor: Color =  MaterialTheme.colors.primaryVariant,
    sizeFilterButton: Dp = LocalConfiguration.current.screenWidthDp.dp,
    searchChange: MutableState<String> = remember { mutableStateOf("") },
    searchBtnTrigger: MutableState<Boolean>,
    noteView: NoteViewModel,
    itemView: ItemViewModel,
    isNote: MutableState<Int>,
    scaffoldState: ScaffoldState,
    focus: FocusManager,
    thread: CoroutineScope,
    iconColor: Color = MaterialTheme.colors.onPrimary,
    painter: Painter?
) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.primary,
//            .drawBehind {
//                val strokeWidth = Stroke.DefaultMiter + 4.0f
//                val y = size.height - strokeWidth / 2
//                drawLine(
//                    borderStrokeColor, Offset(0f, y), Offset(size.width, y), strokeWidth
//                )
//            },
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val animate1 by animateDpAsState(targetValue = if (searchBtnTrigger.value) 0.dp else 70.dp)
                Image(modifier = Modifier
                    .size(animate1)
                    .padding(0.dp),
                    painter = painter!!,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                if (isNote.value >= NOTE) {
                    val animate2 by animateFloatAsState(targetValue = if (searchBtnTrigger.value) 1f else 0f)
                    Search(
                        itemView = itemView,
                        noteView = noteView,
                        searchChange = searchChange,
                        isNote = isNote.value == NOTE,
                        focus = focus,
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth(animate2)
                            .background(Color.Transparent)
                    )
                }
            }
        },
        actions = {
            if (isNote.value >= NOTE) {
                SearchButton(
                    onSearch = {
                        if (searchBtnTrigger.value)
                            if (isNote.value == NOTE) noteView.onSearch(searchChange.value) else itemView.onSearch(searchChange.value)
                        searchBtnTrigger.value = !searchBtnTrigger.value
                        focus.clearFocus()
                        searchChange.value = ""
                    },
                    iconColor = iconColor
                )
                IconButton(
                    modifier = Modifier.size(if (sizeFilterButton >= 600.dp) 50.dp else 35.dp),
                    onClick = { thread.launch { scaffoldState.drawerState.open() } }
                ) {
                    Icon(
                        imageVector = Icons.Sharp.FilterList,
                        contentDescription = "filter",
                        tint = iconColor
                    )
                }
            }
        }
    )
}

//@Composable
//private fun NavigationDrawer(
//    navController: NavHostController,
//    isFullScreen: Boolean,
//    state: DrawerState,
//    thread: CoroutineScope,
//    dataItems: List<NavigationItem> = NavigationLoader().DefaultItem()
//) {
//    ModalDrawer(
//        drawerState = state,
//        drawerShape = RectangleShape,
//        drawerElevation = 12.dp,
//        drawerContent = {
//            LazyColumn (
//                horizontalAlignment = Alignment.Start,
//                contentPadding = PaddingValues(0.dp)
//            ) {
//                items(dataItems) {
//                    Button(
//                        modifier = Modifier
//                            .padding(0.dp, 5.dp)
//                            .padding(15.dp, 5.dp),
//                        border = null,
//                        colors = ButtonDefaults.buttonColors(
//                            Color.Transparent,
//                            MaterialTheme.colors.secondary,
//                            Color.DarkGray,
//                            Color.Gray
//                        ),
//                        shape = RectangleShape,
//                        contentPadding = PaddingValues(10.dp,0.dp),
//                        elevation = ButtonDefaults.elevation(0.dp),
//                        onClick = {
//                            navController.navigate(route = it.route.link)
//                            thread.launch {
//                                state.close()
//                            }
//                        }) {
//                        Icon(
//                            modifier = Modifier.padding(0.dp,0.dp,15.dp,0.dp),
//                            imageVector = it.icon,
//                            contentDescription = stringResource(id = it.route.resId),
//                            tint = MaterialTheme.colors.secondary
//                        )
//                        Text(
//                            text = stringResource(id = it.route.resId),
//                            style = MaterialTheme.typography.h3,
//                            color = MaterialTheme.colors.onBackground
//                        )
//                    }
//                }
//            }
//        }
//    ) {
//        if (isFullScreen)
//            LazyColumn (
//                modifier = Modifier.background(MaterialTheme.colors.secondaryVariant).fillMaxHeight(),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                items(dataItems) {
//                    IconButton(onClick = { navController.navigate(route = it.route.link) }) {
//                        Icon(
//                            imageVector = it.icon,
//                            contentDescription = stringResource(id = it.route.resId),
//                            tint = MaterialTheme.colors.onSecondary
//                        )
//                    }
//                }
//            }
//    }
//}

@Composable
fun Navigation(
    state: MutableState<Int> = remember { mutableStateOf(2) },
    collapseItemHome: MutableState<Boolean>,
    navController: NavHostController,
    windowWidth: WindowWidthSizeClass
) {
    val dataItems: List<NavigationItem> = NavigationLoader.DefaultItem(
        if (windowWidth == WindowWidthSizeClass.Compact) 10 else if (windowWidth == WindowWidthSizeClass.Medium) 20 else 30
    )
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colors.background,
        tonalElevation = 15.dp,
    ) {
        dataItems.forEachIndexed { i, it ->
            NavigationBarItem(
                selected = state.value == i,
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = MaterialTheme.colors.secondaryVariant,
                    unselectedTextColor =  MaterialTheme.colors.secondaryVariant,
                    selectedIconColor = MaterialTheme.colors.onPrimary,
                    selectedTextColor = MaterialTheme.colors.primary,
                    indicatorColor = MaterialTheme.colors.primary.copy(alpha = .2f)
                ),
                onClick = {
                    if (i >= 2) {
                        collapseItemHome.value = !collapseItemHome.value
                        state.value = i
                    } else {
                        navController.navigate(route = it.route.link)
                        if (collapseItemHome.value)
                            collapseItemHome.value = false
                        state.value = i
                    }
                },
                icon = {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = stringResource(id = it.route.resId)
                    )},
                label = {
                    Text(
                        text = stringResource(id = it.route.resId),
                        textAlign = TextAlign.Center,
                        style = if (windowWidth >= WindowWidthSizeClass.Medium)
                            MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                        else MaterialTheme.typography.caption.copy(fontSize = 8.sp)
                    )
                }
            )
        }
    }
}

@Composable
fun CollapseItemHome(
    navController: NavHostController,
    open: MutableState<Boolean>,
    windowWidth: WindowWidthSizeClass,
) {
    val sizeAnimateDp by animateDpAsState(targetValue = if (open.value) 0.dp else 200.dp,
        animationSpec = tween(
            durationMillis = 20,
            delayMillis = if (open.value) 0 else 300,
            easing = LinearOutSlowInEasing
        )
    )
    LazyColumn(
        modifier = Modifier
            .background(Color.Transparent)
            .padding(0.dp, 0.dp, 20.dp, 80.dp)
            .offset(0.dp,sizeAnimateDp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(NavigationLoader.DefaultHomeItem()) {i,it ->
            val sizeAnimate by animateFloatAsState(targetValue = if (open.value) 1f else 0f,
                animationSpec = tween(
                    durationMillis = 400,
                    delayMillis = (if (open.value) 100 else 0) + (i*70),
                    easing = LinearOutSlowInEasing
                )
            )
            FloatingActionButton(
                modifier = Modifier
                    .padding(0.dp)
                    .scale(sizeAnimate)
                    .padding(10.dp),
                elevation = FloatingActionButtonDefaults.elevation(10.dp),
                shape = RoundedCornerShape(50),
                onClick = {
                    navController.navigate(route = it.route.link)
                    open.value = false
                }) {
                Icon(
                    modifier = Modifier
                        .size(if (windowWidth >= WindowWidthSizeClass.Medium) 65.dp else 45.dp)
                        .padding(10.dp),
                    imageVector = it.icon,
                    contentDescription = stringResource(it.route.resId),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, widthDp = 480, heightDp = 800)
@Composable
fun AppBarPreview() {
//    val windowSizeClass = LocalConfiguration.current
//    val width = WindowSizeClass.calculateFromSize(DpSize(windowSizeClass.screenWidthDp.dp,windowSizeClass.screenHeightDp.dp))
//    val navController: NavHostController = rememberNavController()
    BCalcTheme {
        // A surface containcom.amrtm.android.bcalc.component.er using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.surface
        ) {
//            AppBar(navigationController = navController, windowWidth = width.widthSizeClass) {
//                Column(modifier = Modifier.padding(this)) {
//                    Text(text = "Heiiiiii", color = MaterialTheme.colors.onBackground)
//                }
//            }
        }
    }
}

const val NOTE: Int = 1