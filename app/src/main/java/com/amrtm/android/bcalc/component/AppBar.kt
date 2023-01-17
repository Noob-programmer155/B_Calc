package com.amrtm.android.bcalc.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.amrtm.android.bcalc.R
import com.amrtm.android.bcalc.component.data.DataLoader
import com.amrtm.android.bcalc.component.data.NavigationItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppBar(
    openState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    thread: CoroutineScope = rememberCoroutineScope(),
    navigationController: NavHostController,
    isFullScreen: Boolean,
    navigationItems: List<NavigationItem> = DataLoader().DefaultItem(),
    content: @Composable () -> Unit
) {
    TopBar(openState = openState, thread = thread)
    Row {
        NavigationDrawer(navigationController,isFullScreen,openState,navigationItems)
        content
    }
}

@Composable
private fun TopBar(
    openState: DrawerState,
    thread: CoroutineScope,
    borderStrokeColor: Color =  MaterialTheme.colors.primaryVariant
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary)
                    .padding(20.dp, 0.dp)
                    .drawBehind {
                        val strokeWidth = Stroke.DefaultMiter + 5.0f
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            borderStrokeColor, Offset(0f, y), Offset(size.width, y), strokeWidth
                        )
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(modifier = Modifier
                    .padding(15.dp)
                    .size(80.dp)
                    .padding(0.dp),
                    painter = painterResource(id = R.drawable.iconbcalc_1),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Text(text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.h1)
            }
        },
        navigationIcon = {
            IconButton(onClick = {if (openState.isOpen) thread.launch{openState.close()} else thread.launch{openState.open()}}) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = stringResource(id = R.string.menu_btn))
            }
        }
    )
}

@Composable
private fun NavigationDrawer(
    navController: NavHostController,
    isFullScreen: Boolean,
    state: DrawerState,
    dataItems: List<NavigationItem>
) {
    ModalDrawer(
        drawerState = state,
        drawerElevation = 12.dp,
        drawerContent = {
            LazyColumn {
                items(dataItems) {
                    IconButton(onClick = { navController.navigate(route = it.name.link) }) {
                        Icon(imageVector = it.icon, contentDescription = stringResource(id = it.name.resId))
                        if (!isFullScreen)
                            Text(text = stringResource(id = it.name.resId))
                    }
                }
            }
        }
    ) {
        if (isFullScreen)
            LazyColumn {
                items(dataItems) {
                    IconButton(onClick = { navController.navigate(route = it.name.link) }) {
                        Icon(imageVector = it.icon, contentDescription = stringResource(id = it.name.resId))
                    }
                }
            }
    }
}