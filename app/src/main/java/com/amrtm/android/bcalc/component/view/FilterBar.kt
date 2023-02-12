package com.amrtm.android.bcalc.component.view

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.unit.dp
import com.amrtm.android.bcalc.component.data.viewmodel.ItemViewModel
import com.amrtm.android.bcalc.component.data.viewmodel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import java.math.BigDecimal
import java.util.*

@Composable
fun FilterMain(
    noteView: NoteViewModel,
    itemView: ItemViewModel,
    costStartChange: MutableState<BigDecimal> = rememberSaveable { mutableStateOf(BigDecimal.ZERO) },
    costEndChange: MutableState<BigDecimal> = rememberSaveable { mutableStateOf(BigDecimal.ZERO) },
    dateStartChange: MutableState<Date> = rememberSaveable { mutableStateOf(Date()) },
    dateEndChange: MutableState<Date> = rememberSaveable { mutableStateOf(Date()) },
    focus: FocusManager,
    scaffoldState: ScaffoldState,
    thread: CoroutineScope,
    isNote: Boolean,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(10.dp,20.dp),
    ) {
        if (isNote)
            FilterDate(
                scaffoldState = scaffoldState,
                onFilter = { noteView.onFilter(dateStartChange.value,dateEndChange.value) },
                dateStartChange = dateStartChange,
                dateEndChange = dateEndChange,
                thread = thread,
                focus = focus
            )
        else
            FilterBigDecimal(
                scaffoldState = scaffoldState,
                onFilter = { itemView.onFilter(costStartChange.value,costEndChange.value) },
                costStartChange = costStartChange,
                costEndChange = costEndChange,
                thread = thread,
                focus = focus
            )
    }
}

@Composable
fun Search(
    noteView: NoteViewModel,
    itemView: ItemViewModel,
    searchChange: MutableState<String>,
    isNote: Boolean,
    focus: FocusManager,
    modifier: Modifier
) {
    SearchField(
        modifier = modifier,
        onSearchKeyboard = {
            if (isNote) noteView.onSearch(searchChange.value)
            else itemView.onSearch(searchChange.value)
            focus.clearFocus()
                           },
        stateSearchItem = searchChange
    )
}