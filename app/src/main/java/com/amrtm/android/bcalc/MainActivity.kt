package com.amrtm.android.bcalc

import android.content.Context
import android.os.Bundle
import android.widget.ScrollView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amrtm.android.bcalc.component.Home
import com.amrtm.android.bcalc.component.data.DataStorage
import com.amrtm.android.bcalc.component.data.Navigation
import com.amrtm.android.bcalc.ui.theme.BCalcTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BCalcTheme {
                // A surface containcom.amrtm.android.bcalc.component.er using the 'background' color from the theme
                val windowSize = calculateWindowSizeClass(activity = this)
                val storage = DataStorage(this).init()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.surface
                ) {
                    main(windowSize.widthSizeClass, storage)
                }
            }
        }
    }
}

@Composable
fun main(
    windowSize: WindowWidthSizeClass,
    storage: DataStorage,
    navController: NavHostController = rememberNavController()
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        com.amrtm.android.bcalc.component.AppBar(
            navigationController = navController,
            isFullScreen = windowSize >= WindowWidthSizeClass.Medium,
        ) {
            NavHost(
                navController = navController,
                startDestination = Navigation.Home.link
            ) {
                composable(route = Navigation.Home.link) {
                    Home(screenWidth = windowSize,storage)
                }
                composable(route = Navigation.Note.link) {

                }
                composable(route = "${Navigation.Visualize.link}/{type}/{all}/{id}") {
                    val type = it.arguments?.get("type")
                    val all = if(it.arguments?.get("all") == "items") true else false
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
    val windowSize = calculateWindowSizeClass(activity = MainActivity().parent)
    val storage = DataStorage(MainActivity().applicationContext).init()
    BCalcTheme {
        main(windowSize.widthSizeClass,storage)
    }
}