package com.amrtm.android.bcalc.component.view

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.amrtm.android.bcalc.ViewMain
import com.amrtm.android.bcalc.component.data.*
import com.amrtm.android.bcalc.component.data.repository.ItemRaw
import com.amrtm.android.bcalc.component.data.repository.Note
import com.amrtm.android.bcalc.component.data.repository.StatusBalance
import com.amrtm.android.bcalc.component.data.viewmodel.HomeViewModel
import com.amrtm.android.bcalc.component.data.viewmodel.ItemViewModel
import com.amrtm.android.bcalc.component.data.viewmodel.NoteViewModel
import com.amrtm.android.bcalc.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardAddLayout(
    storage: NoteViewModel,
    navController: NavHostController,
    height: Dp,
    modifier: Modifier
) {
    val notes = storage.pagingData.collectAsLazyPagingItems()
    Column(
        modifier = modifier
    ) {
        Card(
            onClick = {navController.navigate(route = Navigation.Note(null).link)},
            modifier = Modifier
                .padding(15.dp, 30.dp, 15.dp, 20.dp)
                .fillMaxWidth()
                .padding(0.dp),
            shape = MaterialTheme.shapes.medium,
            backgroundColor = if (!isSystemInDarkTheme()) defaultNoteColor else defaultNoteColorDark,
            contentColor = MaterialTheme.colors.onPrimary,
            elevation = 20.dp,
//            border = BorderStroke(10.dp, if (!isSystemInDarkTheme()) defaultNoteBorderColor.copy(alpha = .75f) else defaultNoteBorderColorDark.copy(alpha = .75f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
                contentAlignment = Alignment.Center
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = "Add Note", style = MaterialTheme.typography.h6)
                    Icon(
//                      modifier = Modifier.fillMaxSize(.2f),
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = null,
                        tint = if (!isSystemInDarkTheme()) Color.White.copy(alpha = .7f) else Color.LightGray
                    )
                }
            }
        }
        NoteItem(
            notes = notes,
            onClick = { navController.navigate(route = Navigation.Note(it).link)},
            modifier = Modifier
                .padding(0.dp, 20.dp, 0.dp, 80.dp)
                .fillMaxHeight()
                .weight(1f)
                .padding(0.dp)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteItem(
    notes: LazyPagingItems<Note>,
    stateCollapse: MutableState<Long> = remember { mutableStateOf(-1) },
    onClick: (Long) -> Unit,
    modifier: Modifier,
    stateRetry: MutableState<Int> = remember { mutableStateOf(0) }
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        stickyHeader {  }
        itemsIndexed(notes) {_,it ->
            Card(
                modifier = Modifier
                    .padding(0.dp)
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = LinearOutSlowInEasing
                        )
                    )
                    .padding(10.dp, 5.dp, 10.dp, 15.dp),
                onClick = {
                    if (stateCollapse.value == it?.id)
                        stateCollapse.value = -1L
                    else
                        stateCollapse.value = it?.id!!
                },
                enabled = true,
                shape = MaterialTheme.shapes.medium,
                backgroundColor = if (!isSystemInDarkTheme()) defaultNoteColor else defaultNoteColorDark,
                contentColor = MaterialTheme.colors.onPrimary,
                elevation = 5.dp,
//                border = BorderStroke(10.dp, if (!isSystemInDarkTheme()) defaultNoteBorderColor else defaultNoteBorderColorDark)
            ) {
                Column (
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .padding(0.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row (verticalAlignment = Alignment.Top) {
                        Text(text = it?.name!!, style = MaterialTheme.typography.h3, maxLines = 2,
                            softWrap = true, overflow = TextOverflow.Ellipsis)
                        Text(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), textAlign = TextAlign.End,
                            text = SimpleDateFormat("dd/MMM/yyyy", Locale.US).format(it.date),
                            style = MaterialTheme.typography.body1, fontStyle = FontStyle.Italic)
                    }
                    Text(
                        modifier = Modifier.padding(0.dp,5.dp),
                        text = it?.description!!,
                        style = MaterialTheme.typography.body1)
                    Text(text = "* click to see details", style = MaterialTheme.typography.caption)
                    if(stateCollapse.value == it.id) {
                        Divider()
                        Text(text = "Income: ${DecimalFormat("#,###.00").format(it.income)}", style = MaterialTheme.typography.h6)
                        Text(text = "Outcome: ${DecimalFormat("#,###.00").format(it.outcome)}", style = MaterialTheme.typography.h6)
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Status:", style = MaterialTheme.typography.h6)
                            if (it.income- it.outcome > BigDecimal.ZERO)
                                Icon(modifier = Modifier,
                                    tint = StatusBalance.Profit.color,
                                    imageVector = StatusBalance.Profit.icon!!,
                                    contentDescription = null)
                            else if (it.income - it.outcome == BigDecimal.ZERO)
                                Text(text = "=", color = StatusBalance.Balance.color,  style = MaterialTheme.typography.h4)
                            else
                                Icon(modifier = Modifier,
                                    tint = StatusBalance.Loss.color,
                                    imageVector = StatusBalance.Loss.icon!!,
                                    contentDescription = null)
                        }
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
//                            Button(onClick = { navController.navigate(route = "${Navigation.VisualizeNote.link}/item/${it.id}") }) {
//                                Text(text = "Get Visualize", style = MaterialTheme.typography.h5, fontStyle = FontStyle.Italic)
//                            }
//                            Text(modifier = Modifier
//                                .padding(25.dp, 0.dp)
//                                .padding(0.dp),text = "Or", style = MaterialTheme.typography.h5)
                            Button(onClick = {onClick(it.id)}) {
                                Icon(modifier = Modifier.padding(0.dp, 0.dp, 20.dp, 0.dp),imageVector = Icons.Filled.EditNote, contentDescription = "edit")
                                Text(text = "Edit Note", style = MaterialTheme.typography.body1)
                            }
                        }
                    }
                }
            }
        }
        when(notes.loadState.refresh) {
            is LoadState.Loading -> item {
                Box(modifier = Modifier
                    .padding(30.dp)
                    .fillMaxSize()
                    .padding(20.dp),contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = if(isSystemInDarkTheme()) Color.White else Color(0xFF3C79F5))
                }
            }
            is LoadState.Error -> item {
                Column (
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (stateRetry.value <= 3) {
                        Icon(imageVector = Icons.Rounded.ErrorOutline, contentDescription = "error")
                        Text(text = "there is error when loading the data \n would you like to retry it")
                        Button(onClick = {
                            notes.retry()
                            stateRetry.value += 1
                        }) {
                            Icon(
                                modifier = Modifier.padding(0.dp,0.dp,10.dp,0.dp),
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "retry")
                            Text(text = "Retry")
                        }
                    } else {
                        Icon(imageVector = Icons.Rounded.ErrorOutline, contentDescription = "error")
                        Text(text = "its still getting error, maybe will fixed by refresh it")
                        Button(onClick = {
                            notes.refresh()
                            stateRetry.value = 0
                        }) {
                            Icon(
                                modifier = Modifier.padding(0.dp,0.dp,10.dp,0.dp),
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "retry")
                            Text(text = "Refresh")
                        }
                    }
                }
            }
            is LoadState.NotLoading -> item {
                if (notes.itemCount <= 0)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Empty note list, you should add new Note and Item to see this page again")
                    }
            }
            else -> {}
        }
        when(notes.loadState.append) {
            is LoadState.Loading -> item {
                Box(modifier = Modifier
                    .padding(30.dp)
                    .fillMaxWidth()
                    .padding(20.dp),contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = if(isSystemInDarkTheme()) Color.White else Color(0xFF3C79F5))
                }
            }
            is LoadState.Error -> item {
                Column (
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (stateRetry.value <= 3) {
                        Icon(imageVector = Icons.Rounded.ErrorOutline, contentDescription = "error")
                        Text(text = "there is error when loading the data \n would you like to retry it")
                        Button(onClick = {
                            notes.retry()
                            stateRetry.value += 1
                        }) {
                            Icon(
                                modifier = Modifier.padding(0.dp,0.dp,10.dp,0.dp),
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "retry")
                            Text(text = "Retry")
                        }
                    } else {
                        Icon(imageVector = Icons.Rounded.ErrorOutline, contentDescription = "error")
                        Text(text = "its still getting error, maybe will fixed by refresh it")
                        Button(onClick = {
                            notes.refresh()
                            stateRetry.value = 0
                        }) {
                            Icon(
                                modifier = Modifier.padding(0.dp,0.dp,10.dp,0.dp),
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "retry")
                            Text(text = "Refresh")
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun AddNote(
    storage: NoteViewModel,
    homeView: HomeViewModel,
    itemsContainer: ItemViewModel,
    navController: NavHostController,
    modifier: Modifier,
    width: WindowWidthSizeClass,
    title: MutableState<String> = rememberSaveable { mutableStateOf("") },
    description: MutableState<String> = rememberSaveable { mutableStateOf("") },
    income: MutableState<BigDecimal> = rememberSaveable { mutableStateOf(BigDecimal.ZERO) },
    outcome: MutableState<BigDecimal> = rememberSaveable { mutableStateOf(BigDecimal.ZERO) },
    additionalOutcome: MutableState<BigDecimal> = rememberSaveable { mutableStateOf(BigDecimal.ZERO) },
    activeState: MutableState<Boolean> = remember { mutableStateOf(true) },
    thread: CoroutineScope = rememberCoroutineScope(),
    focus: FocusManager
) {
    var itemsList: SnapshotStateList<ItemRaw> = itemsContainer.listItemRaw
    var oldItemsList: List<ItemRaw> = listOf()
    val delete: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }
    val openMessage: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }
    val dataMessage: MutableState<MessageItem> = remember { mutableStateOf(MessageItem()) }
    val stateId = storage.noteId?.toLongOrNull()
    val balance by homeView.balanceData.collectAsState()
    val noteRaw by storage.noteData.collectAsState()
    var note:Note? = null
    if (stateId != null) {
        note = noteRaw.note
        title.value = note.name
        description.value = note.description
        income.value = note.income
        outcome.value = note.outcome
        additionalOutcome.value = note.additionalOutcome
        itemsList = convertToItemRaw(noteRaw.items).toMutableStateList()
        oldItemsList = convertToItemRaw(noteRaw.items)
    }
    // update problem on stock
    Column(
        modifier = modifier
    ) {
        GlobalContainer(title = title, description = description, outcome = additionalOutcome, focus = focus)
        MainProcess(itemsList = itemsList, income = income, outcome = outcome, focus = focus,
            itemView = itemsContainer, onError = openMessage, msg = dataMessage, itemsListOld = oldItemsList)
        SaveOrUpdateOrDelete(
            id = stateId,
            onDelete = {
                focus.clearFocus()
                delete.value = true
                activeState.value = false
            },
            onSave = {
                activeState.value = false
                focus.clearFocus()
                if (title.value.isNotBlank() && description.value.isNotBlank() && itemsList.isNotEmpty()) {
                    val newIncome = balance.income?.add(income.value)
                    val newOutcome = balance.outcome?.add(outcome.value)?.add(additionalOutcome.value)
                    val status = getStatusBalance(newIncome!!,newOutcome!!)
                    val notes = Note(id = null, date = Date(), name = title.value, description = description.value,
                        income = income.value, outcome = outcome.value.add(additionalOutcome.value), additionalOutcome = additionalOutcome.value)
                    var newItemsCount = 0
                    thread.launch {
                        val newId = storage.add(notes)
                        val newItemsData = itemsList.filter { !it.delete }.map {
                            it.copy(stock = (it.stock ?: let {
                            newItemsCount += 1
                            0
                        })) }
                        val newItems = convertToItemHistory(newItemsData,newId,null)
                        itemsContainer.addAll(newItems)
                        homeView.update(balance.copy(
                            income = newIncome,
                            outcome = newOutcome,
                            profit = status.second,
                            lastUpdate = Date(),
                            status = status.first,
                            noteCount = balance.noteCount!! + 1,
                            itemsCount = balance.itemsCount!! + newItemsCount
                        ))
                        clearNote(title,description,income,outcome,additionalOutcome,itemsList)
                        focus.clearFocus()
                        dataMessage.value = dataMessage.value.copy(label = "Saved", description = "Saved Note and Item`s success", error = false)
                        openMessage.value = true
                    }
                } else {
                    dataMessage.value = dataMessage.value.copy(label = "Can`t Save", description = "Name and Description field must not blank.", error = true)
                    openMessage.value = true
                }
            },
            onUpdate = {
                activeState.value = false
                focus.clearFocus()
                val newIncome = balance.income?.add(income.value)?.minus(note?.income ?: BigDecimal.ZERO)
                val newOutcome = balance.outcome?.add(outcome.value)?.add(additionalOutcome.value)?.minus(note?.outcome ?: BigDecimal.ZERO)
                val status = getStatusBalance(newIncome!!,newOutcome!!)
                val notes = note?.copy(name=title.value,description=description.value,
                    income=income.value, outcome = outcome.value, additionalOutcome = additionalOutcome.value)
                var newItemsCount = 0
                thread.launch {
                    storage.update(notes!!)
                    val newItems = convertToItemHistory(itemsList.filter { it.id == null && !it.delete }.map {
                        it.copy(stock = (it.stock ?: let {
                        newItemsCount += 1
                        0
                    })) },notes.id!!,oldItemsList)
                    if (newItems.isNotEmpty())
                        itemsContainer.addAll(newItems)
                    val deleteItems = convertToItemHistory(itemsList.filter { it.delete && it.id != null },notes.id,oldItemsList)
                    if (deleteItems.isNotEmpty())
                        itemsContainer.deleteAll(deleteItems)
                    homeView.update(balance.copy(
                        income = newIncome,
                        outcome = newOutcome,
                        profit = status.second,
                        lastUpdate = Date(),
                        status = status.first,
                        itemsCount = balance.itemsCount!! + newItemsCount
                    ))
                    focus.clearFocus()
                    dataMessage.value = dataMessage.value.copy(label = "Updated", description = "Update Note and Item`s success", error = false)
                    openMessage.value = true
                }
            },
            onAddNew = {
                focus.clearFocus()
                navController.navigate(route = Navigation.Note(null).link)
            },
            onCancel = {
                focus.clearFocus()
                if (stateId != null)
                    navController.navigate(route = Navigation.Note(stateId).link)
                else
                    clearNote(title,description,income,outcome,additionalOutcome,itemsList)
            },
            activeState = activeState,
            width = width
        )
        MessageDialog(
            open = delete,
            title = "Delete Note",
            body = "Are you sure to delete this Notes ??,\n All items associate with this note will be deleted.",
            doneText = "Delete",
            closeText = "Cancel",
            button1Color = Color(0xFFFF0032),
            button2Color = Color(0xFF82AAE3),
            onDismiss = {
                delete.value = false
                activeState.value = true},
            onClose = {
                delete.value = false
                activeState.value = true},
            onDone = {
                thread.launch {
                    if (oldItemsList.isNotEmpty()) {
                        itemsContainer.deleteAll(convertToItemHistory(oldItemsList,note?.id ?: 0,null))
                    }
                    storage.delete(stateId!!)
                    val newIncomes = balance.income?.minus(note?.income ?: BigDecimal.ZERO)
                    val newOutcomes = balance.outcome?.minus(note?.outcome ?: BigDecimal.ZERO)
                    val statuss = getStatusBalance(newIncomes!!, newOutcomes!!)
                    homeView.update(balance.copy(
                        income = newIncomes,
                        outcome = newOutcomes,
                        profit = statuss.second,
                        lastUpdate = Date(),
                        status = statuss.first,
                        noteCount = balance.noteCount!! - 1,
                        itemsCount = itemsContainer.getCountItems()
                    ))
                    delete.value = false
                    activeState.value = true
                    navController.navigate(route = Navigation.Note(null).link)
                }
            },
            error = null
        )
        MessageDialog(
            open = openMessage,
            title = dataMessage.value.label,
            body = dataMessage.value.description,
            doneText = "Close",
            error = dataMessage.value.error,
            withCloseButton = false,
            onDone = {
                openMessage.value = false
                activeState.value = true
                     },
            onDismiss = {
                openMessage.value = false
                activeState.value = true
            }
        )
    }
}

@Composable
private fun GlobalContainer(
    title: MutableState<String>,
    description: MutableState<String>,
    outcome: MutableState<BigDecimal>,
    focus: FocusManager
) {
    Card (
        modifier = Modifier
            .padding(10.dp, 30.dp, 10.dp, 30.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = if (!isSystemInDarkTheme()) defaultNoteColor else defaultNoteColorDark,
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 15.dp,
//        border = BorderStroke(10.dp, if (!isSystemInDarkTheme()) defaultNoteBorderColor else defaultNoteBorderColorDark)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .padding(0.dp),
        ) {
            TextFieldCustom(
                modifier = Modifier,
                value = title,
                onNext = { focus.moveFocus(FocusDirection.Down) },
                label = "Note Name:",
                placeholder = "Name Here",
                color = MaterialTheme.colors.onPrimary,
                textStyle = MaterialTheme.typography.h6
            ).Build()
            TextFieldCustom(
                modifier = Modifier,
                value = description,
                onNext = { focus.moveFocus(FocusDirection.Down) },
                label = "Note Description:",
                placeholder = "Description Here",
                color = MaterialTheme.colors.onPrimary,
                maxLines = 6,
                singleLine = false,
                counterChar = 250,
                textStyle = MaterialTheme.typography.body1
            ).Build()
            TextFieldCustom(
                modifier = Modifier,
                value = outcome,
                onNext = { focus.moveFocus(FocusDirection.Down) },
                label = "Additional Expenses:",
                color = MaterialTheme.colors.onPrimary,
                textStyle = MaterialTheme.typography.h6
            ).Build()
        }
    }
}

@Composable
private fun MainProcess(
    income: MutableState<BigDecimal>,
    outcome: MutableState<BigDecimal>,
    itemsList: SnapshotStateList<ItemRaw>,
    itemsListOld:List<ItemRaw>,
    focus: FocusManager,
    itemView: ItemViewModel,
    onError: MutableState<Boolean>,
    msg: MutableState<MessageItem>
) {
    Card (
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 20.dp)
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 400,
                    easing = LinearOutSlowInEasing
                )
            )
            .padding(20.dp, 20.dp, 20.dp, 30.dp),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = if (!isSystemInDarkTheme()) defaultNoteColor else defaultNoteColorDark,
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 20.dp,
//        border = BorderStroke(10.dp, if (!isSystemInDarkTheme()) defaultNoteBorderColor else defaultNoteBorderColorDark)
    ) {
        Column(
            modifier = Modifier
                .padding(0.dp, 25.dp, 0.dp, 0.dp)
                .fillMaxWidth()
                .padding(0.dp),
            verticalArrangement = Arrangement.Center) {
            Column(
                modifier = Modifier
                    .padding(15.dp, 0.dp)
                    .fillMaxWidth()
                    .padding(0.dp)
            ) {
                Text(text = "Income:", style = MaterialTheme.typography.h4)
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 0.dp, 0.dp, 10.dp),
                    text = "Rp. ${DecimalFormat("#,###.00").format(income.value)}",
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.h6)
                Text(text = "Outcome:", style = MaterialTheme.typography.h4)
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 0.dp, 0.dp, 10.dp),
                    text = "Rp. ${DecimalFormat("#,###.00").format(outcome.value)}",
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.h6)
                Divider(modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 20.dp)
                    .padding(0.dp))
            }
            Column(
                modifier = Modifier
                    .padding(15.dp, 0.dp)
                    .fillMaxWidth()
                    .padding(0.dp)
            ) {
                for(it in itemsList.filter { !it.delete }) {
                    ItemModifableComponent(data = itemsList, item = it, income = income, outcome = outcome)
                }
            }
            ItemAddModifableComponent(itemsList,itemsListOld,income, focus = focus, outcome = outcome,itemView = itemView, onError = onError, msg = msg)
        }
    }
}

@Composable
private fun ItemAddModifableComponent(
    data:SnapshotStateList<ItemRaw>,
    oldData:List<ItemRaw>,
    income: MutableState<BigDecimal>,
    outcome: MutableState<BigDecimal>,
    itemName: MutableState<String> = rememberSaveable { mutableStateOf("") },
    itemBuyCost: MutableState<BigDecimal> = rememberSaveable { mutableStateOf(BigDecimal.ZERO) },
    itemSellCost: MutableState<BigDecimal> = rememberSaveable { mutableStateOf(BigDecimal.ZERO) },
    itemBuyed: MutableState<Int> = rememberSaveable { mutableStateOf(0) },
    itemSold: MutableState<Int> = rememberSaveable { mutableStateOf(0) },
    itemDiscount: MutableState<Int> = rememberSaveable { mutableStateOf(0) },
    collapse: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) },
    thread: CoroutineScope = rememberCoroutineScope(),
    itemView: ItemViewModel,
    onError: MutableState<Boolean>,
    msg: MutableState<MessageItem>,
    focus: FocusManager
) {
    Card (
        modifier = Modifier
            .padding(0.dp, 10.dp, 0.dp, 0.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = if (isSystemInDarkTheme()) defaultNoteColor.copy(alpha = .75f) else defaultNoteColorDark.copy(alpha = .75f),
        contentColor = MaterialTheme.colors.onPrimary,
        border = null
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(5.dp, 20.dp, 5.dp, 0.dp)
                    .fillMaxWidth()
                    .padding(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextFieldCustom(
                    modifier = Modifier,
                    value = itemName,
                    onNext = { focus.moveFocus(FocusDirection.Down) },
                    label = "Item Name:",
                    placeholder = "Name",
                    color = MaterialTheme.colors.onPrimary,
                    textStyle = MaterialTheme.typography.h6
                ).Build()
                TextFieldCustom(
                    modifier = Modifier,
                    value = itemBuyCost,
                    onNext = { focus.moveFocus(FocusDirection.Down) },
                    label = "Item Buy Cost:",
                    color = MaterialTheme.colors.onPrimary,
                    textStyle = MaterialTheme.typography.h6
                ).Build()
                TextFieldCustom(
                    modifier = Modifier,
                    value = itemSellCost,
                    onNext = { focus.moveFocus(FocusDirection.Down) },
                    label = "Item Sell Cost:",
                    color = MaterialTheme.colors.onPrimary,
                    textStyle = MaterialTheme.typography.h6
                ).Build()
                TextFieldCustom(
                    modifier = Modifier,
                    value = itemBuyed,
                    onNext = { focus.moveFocus(FocusDirection.Down) },
                    label = "Buy Item Count:",
                    color = MaterialTheme.colors.onPrimary,
                    textStyle = MaterialTheme.typography.h6
                ).Build()
                TextFieldCustom(
                    modifier = Modifier,
                    value = itemSold,
                    onNext = { focus.moveFocus(FocusDirection.Down) },
                    label = "Sold Items Count:",
                    color = MaterialTheme.colors.onPrimary,
                    textStyle = MaterialTheme.typography.h6
                ).Build()
                if (collapse.value) {
                    TextFieldCustom(
                        modifier = Modifier,
                        value = itemDiscount,
                        onNext = { focus.moveFocus(FocusDirection.Down) },
                        label = "Discount Per Items(0-100)",
                        currencySymbolIcon = Icons.Rounded.Discount,
                        color = MaterialTheme.colors.onPrimary,
                        endSymbol = "%",
                        textStyle = MaterialTheme.typography.h6,
                        additionalInfo = "sell cost after discount: Rp. ${DecimalFormat("#,###")
                            .format(itemSellCost.value
                                .multiply(BigDecimal.valueOf(100-itemDiscount.value.toLong()))
                                .divide(BigDecimal.valueOf(100)))}.00"
                    ).Build()
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.onPrimary.copy(alpha = .15f)
                ),
                onClick = { collapse.value = !collapse.value }
            ) {
                val anim1 by animateFloatAsState(targetValue = if (collapse.value) 180f else 0f,
                    animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing))
                Icon(
                    modifier = Modifier
                        .size(50.dp)
                        .rotate(anim1),
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = "expand more")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                enabled = itemName.value.isNotBlank() && itemBuyCost.value != BigDecimal.ZERO && itemSellCost.value != BigDecimal.ZERO &&
                        itemBuyed.value != -1 && itemSold.value != -1,
                onClick = {
                    focus.clearFocus()
                    var itemOld: List<ItemRaw> = listOf()
                    if (oldData.isNotEmpty())
                        itemOld = oldData.filter { item ->
                            val dataf = data.filter { item.delete && item.name == itemName.value.uppercase() }
                            if (dataf.isNotEmpty())
                                item.name == dataf.first().name
                            else
                                false
                        }
                    thread.launch {
                        val stock = itemView.getStock(itemName.value.uppercase())
                        val dt = ItemRaw(
                            name = itemName.value.uppercase(),
                            buyCost = itemBuyCost.value,
                            sellCost = itemSellCost.value,
                            discount = itemDiscount.value,
                            sold_out = itemSold.value,
                            purchased = itemBuyed.value,
                            stock = stock,
                            total = itemSellCost.value.multiply(BigDecimal.valueOf((100 - itemDiscount.value).toLong()))
                                .multiply(BigDecimal.valueOf(itemSold.value.toLong())).divide(BigDecimal.valueOf(100))
                                .minus(itemBuyCost.value.multiply(BigDecimal.valueOf(itemBuyed.value.toLong())))
                        )
                        if ((dt.stock ?: 0) + dt.purchased - dt.sold_out
                            + (if (itemOld.isNotEmpty()) itemOld.first().sold_out - itemOld.first().purchased else 0) >= 0) {
                            data.add(dt)
                            income.value = income.value.add(dt.total)
                            outcome.value = outcome.value.add(itemBuyCost.value.multiply(BigDecimal.valueOf(itemBuyed.value.toLong())))
                            clearItem(itemName,itemBuyCost,itemSellCost,itemDiscount,itemSold,itemBuyed)
                        } else {
                            msg.value = msg.value.copy(label = "Out of Stock", description = "Stock is less than zero,\nyour current stock: ${stock}", error = true)
                            onError.value = true
                        }
                    }
                }) {
                    Icon(modifier = Modifier
                        .padding(10.dp,15.dp),
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Add Note",
                        tint = if (!isSystemInDarkTheme()) Color.White else Color.LightGray
                    )
                    Text(text = "Add Item")
            }
        }
    }
}

@Composable
private fun ItemModifableComponent(
    income: MutableState<BigDecimal>,
    outcome: MutableState<BigDecimal>,
    data:SnapshotStateList<ItemRaw>,
    item: ItemRaw
) {
    Card (
        modifier = Modifier
            .padding(0.dp, 20.dp, 0.dp, 0.dp)
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = if (isSystemInDarkTheme()) defaultNoteColor else defaultNoteColorDark,
        contentColor = MaterialTheme.colors.onPrimary
//        border = BorderStroke(2.dp, if (isSystemInDarkTheme()) defaultNoteBorderColor else defaultNoteBorderColorDark)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .padding(0.dp)
        ) {
            Text(text = item.name, style = MaterialTheme.typography.h4)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = "Buy Cost:", style = MaterialTheme.typography.body1.copy(fontSize = 11.sp))
                    Text(text = "Rp. ${DecimalFormat("#,###.00").format(item.buyCost)}", style = MaterialTheme.typography.h5)
                    Text(text = "Sell Cost:", style = MaterialTheme.typography.body1.copy(fontSize = 11.sp))
                    Text(text = "Rp. ${DecimalFormat("#,###.00").format(item.sellCost)}", style = MaterialTheme.typography.h5)
                }
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f))
                Column {
                    Column{
                        Text(
                            modifier = Modifier.padding(0.dp,0.dp,0.dp,5.dp),
                            text = "Discount:",
                            style = MaterialTheme.typography.body1.copy(fontSize = 11.sp))
                        Row {
                            Icon(imageVector = Icons.Rounded.Discount, contentDescription = null)
                            Text(text = "${item.discount} %")
                        }
                    }
                    Spacer(modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f))
                    Text(
                        modifier = Modifier.padding(0.dp,0.dp,0.dp,5.dp),
                        text = "Buy|Sold:")
                    Text(text = "${item.purchased}|${item.sold_out}")
                }
            }
            Text(text = "Total:", style = MaterialTheme.typography.h4)
            Text(text = "Rp. ${DecimalFormat("#,###.00").format(item.total)}", style = MaterialTheme.typography.h5, textAlign = TextAlign.End)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    data[data.indexOf(item)] = item.copy(delete = true)
                    income.value = income.value.minus(item.total)
                    outcome.value = outcome.value.minus(item.buyCost.multiply(BigDecimal.valueOf(item.purchased.toLong())))
                }) {
                    Icon(imageVector = Icons.Rounded.Delete,contentDescription = "delete", tint = MaterialTheme.colors.error)
                }
            }
        }
    }
}

@Composable
private fun SaveOrUpdateOrDelete(
    id: Long?,
    onDelete: () -> Unit,
    onSave: () -> Unit,
    onUpdate: () -> Unit,
    onCancel: () -> Unit,
    onAddNew: () -> Unit,
    activeState: MutableState<Boolean>,
    width: WindowWidthSizeClass
) {
    Card(
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 100.dp)
            .fillMaxWidth()
            .padding(10.dp),
        shape = MaterialTheme.shapes.large,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
        elevation = 15.dp
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .padding(0.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if(id != null) {
                LazyHorizontalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (width == WindowWidthSizeClass.Compact) 100.dp else 60.dp),
                    rows = GridCells.Adaptive(minSize = 40.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    contentPadding = PaddingValues(5.dp)
                ) {
                    item {
                        Button(
                            modifier = Modifier.width(100.dp),
                            enabled = activeState.value,
                            onClick = onCancel
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                    item {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.error.copy(alpha = .75f),
                                contentColor = Color.White
                            ),
                            enabled = activeState.value,
                            onClick = onDelete
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "delete note"
                            )
                            Text(text = "Delete", maxLines = 2, softWrap = true)
                        }
                    }
                    item {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFFFB100),
                                contentColor = Color.White
                            ),
                            enabled = activeState.value,
                            onClick = onAddNew
                        ) {
                            Icon(imageVector = Icons.Filled.AddCircle,contentDescription = "add new note")
                            Text(text = " New Note", maxLines = 2, softWrap = true)
                        }
                    }
                    item {
                        Button(
                            modifier = Modifier.width(120.dp),
                            enabled = activeState.value,
                            onClick = onUpdate
                        ) {
                            Text(text = "Update")
                        }
                    }
                }
            } else {
                Button(
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 10.dp, 0.dp)
                        .fillMaxWidth()
                        .padding(0.dp)
                        .weight(1f),
                    enabled = activeState.value,
                    onClick = onCancel
                ) {
                    Text(text = "Cancel")
                }
                Button(
                    enabled = activeState.value,
                    onClick = onSave
                ) {
                    Icon(imageVector = Icons.Rounded.Save,contentDescription = "save note")
                    Text(text = "Save")
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 480, heightDp = 800)
@Composable
fun NoteAddPreview() {
//    val navController: NavHostController = rememberNavController()
//    val view: HomeViewModel = viewModel(factory = ViewMain.Factory)
    BCalcTheme {
        // A surface containcom.amrtm.android.bcalc.component.er using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.surface
        ) {
            Home(padding = 30.dp, numberPage = 1) {
//                AddNote(
//                    homeView = view,
//                    navController = navController,
//                    modifier = Modifier.padding(30.dp)
//                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 480, heightDp = 800)
@Composable
fun NotesPreview() {
//    val navController: NavHostController = rememberNavController()
//    val context = LocalContext.current
    BCalcTheme {
        // A surface containcom.amrtm.android.bcalc.component.er using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.surface
        ) {
            Home(padding = 30.dp, numberPage = 1) {
//                (
//                    windowSize = WindowWidthSizeClass.Compact,
//                    navController = navController,
//                    height = 400.dp,
//                    context = context
//                )
            }
        }
    }
}