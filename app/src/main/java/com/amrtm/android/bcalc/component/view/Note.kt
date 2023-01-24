package com.amrtm.android.bcalc.component.view

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
import com.amrtm.android.bcalc.ViewMain
import com.amrtm.android.bcalc.component.data.*
import com.amrtm.android.bcalc.component.data.repository.ItemRaw
import com.amrtm.android.bcalc.component.data.repository.Note
import com.amrtm.android.bcalc.component.data.repository.StatusBalance
import com.amrtm.android.bcalc.component.data.viewmodel.HomeViewModel
import com.amrtm.android.bcalc.component.data.viewmodel.ItemViewModel
import com.amrtm.android.bcalc.component.data.viewmodel.NoteViewModel
import com.amrtm.android.bcalc.ui.theme.defaultNoteBorderColor
import com.amrtm.android.bcalc.ui.theme.defaultNoteBorderColorDark
import com.amrtm.android.bcalc.ui.theme.defaultNoteColor
import com.amrtm.android.bcalc.ui.theme.defaultNoteColorDark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardAddLayout(
    storage: NoteViewModel = viewModel(factory = ViewMain.Factory),
    navController: NavHostController,
    height: Dp,
    windowSize: WindowWidthSizeClass,
    collapse: HomeViewModel,
    context: Context
) {
    storage.pageSize.value = (if (windowSize == WindowWidthSizeClass.Compact) 5 else if (windowSize == WindowWidthSizeClass.Medium) 10 else 15)
    val notes = storage.pagingData.collectAsLazyPagingItems()
    Card(
        onClick = {navController.navigate(route = Navigation.Note.link)},
        modifier = Modifier
            .padding(0.dp, 20.dp, 0.dp, 0.dp)
            .fillMaxWidth()
            .padding(0.dp),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = if (!isSystemInDarkTheme()) defaultNoteColor.copy(alpha = .75f) else defaultNoteColorDark.copy(alpha = .75f),
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 20.dp,
        border = BorderStroke(10.dp, if (!isSystemInDarkTheme()) defaultNoteBorderColor.copy(alpha = .75f) else defaultNoteBorderColorDark.copy(alpha = .75f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            contentAlignment = Alignment.Center
        ) {
            Icon(modifier = Modifier.fillMaxSize(.2f),
                imageVector = Icons.Filled.AddCircle,
                contentDescription = null,
                tint = if (!isSystemInDarkTheme()) Color.White else Color.LightGray
            )
        }
    }
    Row (verticalAlignment = Alignment.CenterVertically) {
        SearchField(view = storage)
        Filter(
            view = storage,
            context = context,
        )
    }
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        itemsIndexed(notes) {_,it ->
            Card(
                onClick = {
                    if (collapse.collapseCard.value == it?.id)
                        collapse.setCollapseCard(-2)
                    else
                        collapse.setCollapseCard(it?.id!!)
                },
                enabled = true,
                modifier = Modifier
                    .padding(0.dp, 25.dp, 0.dp, 0.dp)
                    .fillMaxWidth()
                    .padding(15.dp)
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 1000,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                shape = MaterialTheme.shapes.medium,
                backgroundColor = if (!isSystemInDarkTheme()) defaultNoteColor else defaultNoteColorDark,
                contentColor = MaterialTheme.colors.onPrimary,
                elevation = 35.dp,
                border = BorderStroke(10.dp, if (!isSystemInDarkTheme()) defaultNoteBorderColor else defaultNoteBorderColorDark)
            ) {
                Column (horizontalAlignment = Alignment.Start) {
                    Row (verticalAlignment = Alignment.Top) {
                        Text(text = it?.name!!, style = MaterialTheme.typography.h3, maxLines = 2,
                            softWrap = true, overflow = TextOverflow.Ellipsis)
                        Text(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), textAlign = TextAlign.End,
                            text = SimpleDateFormat("dd/MMM/yyyy", Locale.US).format(it.date),
                            style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
                    }
                    Text(text = "Income: ${DecimalFormat("#,###.00").format(it?.income)}", style = MaterialTheme.typography.h6)
                    Text(text = "Outcome: ${DecimalFormat("#,###.00").format(it?.outcome)}", style = MaterialTheme.typography.h6)
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Status:", style = MaterialTheme.typography.h6)
                        if (it?.income!!- it.outcome > BigDecimal.ZERO)
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
                    if(collapse.collapseCard.value == it?.id) {
                        Divider()
                        Text(
                            textAlign = TextAlign.End,
                            text = "get detail and visualize data",
                            style = MaterialTheme.typography.body1,
                            fontStyle = FontStyle.Italic
                        )
                        Row (horizontalArrangement = Arrangement.End) {
                            Button(onClick = { navController.navigate(route = "${Navigation.VisualizeNote.link}/item/${it.id}") }) {
                                Text(text = "Get Visualize", style = MaterialTheme.typography.h5, fontStyle = FontStyle.Italic)
                            }
                            Text(modifier = Modifier
                                .padding(25.dp, 0.dp)
                                .padding(0.dp),text = "Or", style = MaterialTheme.typography.h5)
                            Button(onClick = { navController.navigate(route = "${Navigation.Note.link}/${it.id}") }) {
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
                    modifier = Modifier.fillParentMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(text = "there is error when loading the data")
                    Icon(imageVector = Icons.Rounded.ErrorOutline, contentDescription = "error")
                }
            }
            else -> {}
        }
        when(notes.loadState.append) {
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
                    modifier = Modifier.fillParentMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(text = "there is error when loading the data")
                    Icon(imageVector = Icons.Rounded.ErrorOutline, contentDescription = "error")
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun SearchField(
    view: NoteViewModel,
    searchChange: MutableState<String> = remember { mutableStateOf("") }
) {
    OutlinedTextField(
        value = searchChange.value,
        onValueChange = { searchChange.value = it },
        placeholder = { Text(text = "Search...", style = MaterialTheme.typography.subtitle1)},
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            view.onSearch(searchChange.value)
        }),
        trailingIcon = { SearchButton(view = view,searchChange = searchChange)}
    )
}

@Composable
private fun SearchButton(
    view: NoteViewModel,
    searchChange: MutableState<String>
) {
    IconButton(onClick = {
        view.onSearch(searchChange.value)
    }) {
        Icon(imageVector = Icons.Rounded.Search, contentDescription = "search note")
    }
}

//==================================================================================================================================
@Composable
fun AddNote(
    storage: NoteViewModel = viewModel(factory = ViewMain.Factory),
    id: Int?,
    padding: Dp,
    title: MutableState<String> = remember { mutableStateOf("") },
    description: MutableState<String> = remember { mutableStateOf("") },
    income: MutableState<BigDecimal> = remember { mutableStateOf(BigDecimal.ZERO) },
    outcome: MutableState<BigDecimal> = remember { mutableStateOf(BigDecimal.ZERO) },
    itemsList: SnapshotStateList<ItemRaw> = remember { mutableStateListOf() },
    thread: CoroutineScope = rememberCoroutineScope()
) {
    val delete: MutableState<Boolean> = remember { mutableStateOf(false) }
    val save: MutableState<Boolean> = remember { mutableStateOf(false) }
    val itemsContainer: ItemViewModel = viewModel(factory = ViewMain.Factory)
    var note:Note? = null
    var itemsIdRef: List<Int>? = null
    if (id != null) {
        val noteRaw = storage.noteData(id).collectAsState(
            initial = mapOf(Pair(Note(null,Date(),"","", BigDecimal.ZERO, BigDecimal.ZERO), listOf()))
        )
        note = noteRaw.value.keys.first()
        title.value = note.name
        description.value = note.description
        income.value = note.income
        outcome.value = note.outcome
        itemsList.addAll(convertToItemRaw(noteRaw.value.get(note)!!))
        itemsIdRef = noteRaw.value.get(note)!!.map { it.id ?: 0 }
    }
    Column(modifier = Modifier.padding(padding)) {
        GlobalContainer(title = title, description = description, outcome = outcome)
        MainProcess(itemsList = itemsList, income = income, outcome = outcome)
        SaveOrUpdateOrDelete(
            id = id,
            onDelete = { delete.value = true },
            onSave = {
                if (title.value.isNotBlank() && description.value.isNotBlank()) {
                    val notes = Note(id = null, date = Date(), name = title.value, description = description.value,
                        income = income.value, outcome = outcome.value)
                    thread.launch {
                        val newId = storage.add(notes)
                        val newItems = convertToItem(itemsList,newId)
                        itemsContainer.addAll(newItems)
                    }
                } else {
                    save.value = true
                }},
            onUpdate = {
                val notes = note?.copy(name=title.value,description=description.value,
                    income=income.value, outcome = outcome.value)
                storage.update(notes!!)
                val newItems = convertToItem(itemsList.filter { it.id == null},notes.id!!)
                thread.launch {
                    itemsContainer.addAll(newItems)
                }
                val deleteItems = getIdFromList(itemsList.filter { it.delete })
                itemsContainer.deleteAll(deleteItems)
            })
        MessageDialog(
            open = delete,
            title = "Delete Note",
            body = { Text(text = "Are you sure to delete this Notes ??,\n All items associate with this note will be deleted.", style = MaterialTheme.typography.body1) },
            doneText = "Delete",
            closeText = "Cancel",
            onDone = {
                if (itemsIdRef?.isNotEmpty()!!) {
                    itemsContainer.deleteAll(itemsIdRef)
                }
                storage.delete(id!!)
                delete.value = false
            }
        )
        MessageDialog(
            open = save,
            title = "Can`t Save",
            body = { Text(text = "Name and Description field must not blank.", style = MaterialTheme.typography.body1) },
            doneText = "Close",
            withCloseButton = false,
            onDone = { save.value = false },
            onDismiss = { save.value = false }
        )
    }
}

@Composable
private fun GlobalContainer(
    title: MutableState<String>,
    description: MutableState<String>,
    outcome: MutableState<BigDecimal>
) {
    Card (
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 50.dp)
            .fillMaxWidth()
            .padding(15.dp),
        shape = MaterialTheme.shapes.large,
        backgroundColor = if (!isSystemInDarkTheme()) defaultNoteColor else defaultNoteColorDark,
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 20.dp,
        border = BorderStroke(10.dp, if (!isSystemInDarkTheme()) defaultNoteBorderColor else defaultNoteBorderColorDark)
    ) {
        Column() {
            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = {Text(text = "Note Name")}
            )
            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = {Text(text = "Note Description")}
            )
            OutlinedTextField(
                value = DecimalFormat("#,###.00").format(outcome.value),
                onValueChange = { outcome.value = DecimalFormat("#,###.00").parse(it) as BigDecimal },
                leadingIcon = { Text(text = "Rp. ",style = MaterialTheme.typography.h6) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                label = { Text(text = "expense") }
            )
        }
    }
}

@Composable
private fun MainProcess(
    income: MutableState<BigDecimal>,
    outcome: MutableState<BigDecimal>,
    itemsList: SnapshotStateList<ItemRaw>
) {
    Card (
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 50.dp)
            .fillMaxWidth()
            .padding(15.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = MaterialTheme.shapes.large,
        backgroundColor = if (!isSystemInDarkTheme()) defaultNoteColor else defaultNoteColorDark,
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 20.dp,
        border = BorderStroke(10.dp, if (!isSystemInDarkTheme()) defaultNoteBorderColor else defaultNoteBorderColorDark)
    ) {
        Column(modifier = Modifier.animateContentSize(),verticalArrangement = Arrangement.Center) {
            Text(text = "Income:", style = MaterialTheme.typography.h4)
            Text(text = "Rp. ${DecimalFormat("#,###.00").format(income.value)}",style = MaterialTheme.typography.h6)
            Text(text = "Outcome:", style = MaterialTheme.typography.h4)
            Text(text = "Rp. ${DecimalFormat("#,###.00").format(outcome.value)}",style = MaterialTheme.typography.h6)
            Divider(modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 40.dp)
                .padding(0.dp))
            LazyColumn {
                items(itemsList.filter { !it.delete }) {
                    ItemModifableComponent(data = itemsList, item = it, income = income)
                }
            }
            ItemAddModifableComponent(itemsList,income)
        }
    }
}/*TODO*/

@Composable
private fun ItemAddModifableComponent(
    data:SnapshotStateList<ItemRaw>,
    income: MutableState<BigDecimal>,
    itemName: MutableState<String> = remember { mutableStateOf("") },
    itemCost: MutableState<BigDecimal> = remember { mutableStateOf(BigDecimal.ZERO) },
    itemStock: MutableState<Int> = remember { mutableStateOf(-1) },
    itemSold: MutableState<Int> = remember { mutableStateOf(-1) },
    itemDiscount: MutableState<Int> = remember { mutableStateOf(0) },
    collapse: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    Card (
        modifier = Modifier
            .padding(0.dp, 20.dp, 0.dp, 0.dp)
            .fillMaxWidth()
            .padding(0.dp),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = if (isSystemInDarkTheme()) defaultNoteColor.copy(alpha = .75f) else defaultNoteColorDark.copy(alpha = .75f),
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 10.dp,
        border = null
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = itemName.value,
                onValueChange = { itemName.value = it },
                label = { Text(text = "Item Name")})
            TextField(
                value = DecimalFormat("#,###.00").format(itemCost.value),
                onValueChange = { itemCost.value = DecimalFormat("#,###.00").parse(it) as BigDecimal },
                leadingIcon = { Text(text = "Rp. ",style = MaterialTheme.typography.h6) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                label = { Text(text = "Item Cost")})
            TextField(
                value = itemStock.value.toString(),
                onValueChange = { itemStock.value = it.toInt() },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                label = { Text(text = "In Stock")})
            TextField(
                value = itemSold.value.toString(),
                onValueChange = { itemSold.value = it.toInt()},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                label = { Text(text = "sold Items")})
            if (collapse.value) {
                TextField(
                    value = itemDiscount.value.toString(),
                    placeholder = { Text(text = "Discount Per Items") },
                    label = { Text(text = "range 0 - 100") },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.Discount, contentDescription = "discount item") },
                    trailingIcon = { Text(text = "%",style = MaterialTheme.typography.h6) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    onValueChange = { if (it.toInt() <= 100) itemDiscount.value = it.toInt() })
            }
            Button(onClick = { collapse.value = !collapse.value }) {
                Icon(
                    modifier = Modifier
                        .rotate(if (collapse.value) 180f else 0f)
                        .animateContentSize(
                            animationSpec = tween(
                                durationMillis = 1000,
                                easing = FastOutLinearInEasing
                            )
                        ),
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = "expand more")
            }
            Divider(modifier = Modifier.padding(0.dp,50.dp))
            Button(
                enabled = itemName.value.isNotBlank() && itemCost.value != BigDecimal.ZERO && itemStock.value != -1 && itemSold.value != -1,
                onClick = {
                    val dt = ItemRaw(
                        name = itemName.value.uppercase(),
                        cost = itemCost.value,
                        discount = itemDiscount.value,
                        sold_out = itemSold.value,
                        stock = itemStock.value,
                        total = itemCost.value.multiply(BigDecimal.valueOf(itemSold.value.toLong())).multiply(BigDecimal.valueOf(
                            (100 - itemDiscount.value).toLong())))
                    data.add(dt)
                    income.value.add(dt.total)
            }) {
                Icon(modifier = Modifier
                    .fillMaxSize(.2f)
                    .padding(25.dp),
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = null,
                    tint = if (!isSystemInDarkTheme()) Color.White else Color.LightGray
                )
                Text(text = "Add New")
            }
        }
    }
}

@Composable
private fun ItemModifableComponent(
    income: MutableState<BigDecimal>,
    data:SnapshotStateList<ItemRaw>,
    item: ItemRaw
) {
    Card (
        modifier = Modifier
            .padding(0.dp, 20.dp, 0.dp, 0.dp)
            .fillMaxWidth()
            .padding(12.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = MaterialTheme.shapes.medium,
        backgroundColor = if (isSystemInDarkTheme()) defaultNoteColor else defaultNoteColorDark,
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 10.dp,
        border = BorderStroke(2.dp, if (isSystemInDarkTheme()) defaultNoteBorderColor else defaultNoteBorderColorDark)
    ) {
        Column {
            Text(text = item.name)
            Row {
                Text(text = "${item.cost}")
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f))
                Icon(imageVector = Icons.Rounded.Discount, contentDescription = null)
                Text(text = "${item.discount} %")
            }
            Row {
                Text(text = "Stock|Sold:")
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f))
                Text(text = "${item.stock}|${item.sold_out}")
            }
            IconButton(onClick = {
                data.set(data.indexOf(item),item.copy(delete = true))
                income.value.min(item.total)
            }) {
                Icon(imageVector = Icons.Rounded.Delete,contentDescription = "delete")
            }
        }
    }
}

@Composable
private fun SaveOrUpdateOrDelete(
    id: Int?,
    onDelete: () -> Unit,
    onSave: () -> Unit,
    onUpdate: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 50.dp)
            .fillMaxWidth()
            .padding(15.dp),
        shape = MaterialTheme.shapes.large,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
        elevation = 15.dp
    ) {
        Row {
            if(id != null) {
                Button(onClick = onUpdate) {
                    Text(text = "Update")
                }
                IconButton(
                    modifier = Modifier
                        .padding(30.dp, 0.dp, 0.dp, 0.dp)
                        .background(MaterialTheme.colors.error)
                        .padding(15.dp),
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "delete note",
                        tint = MaterialTheme.colors.onError
                    )
                }
            } else
                Button(onClick = onSave) {
                    Icon(imageVector = Icons.Rounded.Save,contentDescription = "save note")
                    Text(text = "Save")
                }
        }
    }
}