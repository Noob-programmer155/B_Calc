package com.amrtm.android.bcalc.component

import android.content.Context
import android.graphics.drawable.Icon
import androidx.annotation.IdRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.amrtm.android.bcalc.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    openState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    thread: CoroutineScope = rememberCoroutineScope(),
    isFullScreen: Boolean
) {
    Scaffold(
        topBar = {TopBar(openState = openState, thread = thread)},
//        floatingActionButton = {
//            IconButton(onClick = {  }) {
//                Icon(
//                    modifier = Modifier.rotate(180f),
//                    imageVector = Icons.Default.ArrowDropDown,
//                    contentDescription = stringResource(id = R.string.scroll_to_top))
//            }
//        },
//        floatingActionButtonPosition = FabPosition.End
    ) {
        NavigationDrawer(isFullScreen,it)

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
                    .padding(1.dp, 0.dp)
                    .background(MaterialTheme.colors.primary)
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
                    .size(64.dp)
                    .padding(.5.dp),
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NavigationDrawer(
    isFullScreen: Boolean,
    padding: PaddingValues,
    dataItems: List<>
) {
    ModalDrawer(
        modifier = Modifier.padding(padding)
            .consumedWindowInsets(padding),
        drawerContent = {
            Column {
                items()
            }
        }
    ) {
        if (isFullScreen)
            Column {
                
            }
    }
}

class NavigationItem(
    name: String,
    icon: Icon,
    link: String
    ) {

}