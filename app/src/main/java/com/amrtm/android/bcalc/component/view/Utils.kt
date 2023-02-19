package com.amrtm.android.bcalc.component.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amrtm.android.bcalc.component.data.repository.Item
import com.amrtm.android.bcalc.component.data.repository.ItemHistory
import com.amrtm.android.bcalc.component.data.repository.ItemRaw
import com.amrtm.android.bcalc.component.data.repository.StatusBalance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun getStatusBalance(income: BigDecimal, outcome: BigDecimal): Pair<StatusBalance,BigDecimal> {
    val add = income.minus(outcome)
    return Pair(if (add > BigDecimal.ZERO) StatusBalance.Profit else if (add < BigDecimal.ZERO) StatusBalance.Loss else StatusBalance.Balance,add)
}

fun convertToItem(itemRaws: List<ItemHistory>): List<Item> {
    return itemRaws.map {
        Item(
            id = it.id,
            name = it.name.uppercase(),
            buyCost = it.buyCost,
            sellCost = it.sellCost,
            discount = it.discount,
            sold_out = it.sold_out,
            purchased = it.purchased,
            stock = it.stock,
            total_item = it.total_item,
            total = it.total,
            date = it.date
        )
    }
}

fun convertToItemHistory(itemRaws: List<ItemRaw>, noteId: Long, oldItemRawsDeleted: List<ItemRaw>?): List<ItemHistory> {
    return itemRaws.map {
        var data = listOf<ItemRaw>()
        if (oldItemRawsDeleted != null)
            data = oldItemRawsDeleted.filter {item -> item.name == it.name }
        ItemHistory(
            id = it.id,
            note = noteId,
            name = it.name.uppercase(),
            buyCost = it.buyCost,
            sellCost = it.sellCost,
            discount = it.discount,
            sold_out = it.sold_out,
            purchased = it.purchased,
            stock = it.purchased + (it.stock ?: 0) - it.sold_out
                    + if (data.isNotEmpty()) data.first().sold_out - data.first().purchased else 0,
            total_item = it.sold_out + it.purchased + (it.stock ?: 0)
                    + if (data.isNotEmpty()) - data.first().sold_out - data.first().purchased else 0,
            total = it.total,
            date = Date()
        )
    }
}

fun convertToItemRaw(items: List<ItemHistory>): List<ItemRaw> {
    return items.map {
        ItemRaw(
            id = it.id,
            name = it.name.uppercase(),
            buyCost = it.buyCost,
            sellCost = it.sellCost,
            discount = it.discount,
            sold_out = it.sold_out,
            purchased = it.purchased,
            total = it.total,
            date = it.date,
            stock = it.stock
        )
    }
}

@Composable
fun SearchField(
    modifier: Modifier,
    onSearchKeyboard: KeyboardActionScope.() -> Unit,
    stateSearchItem: MutableState<String>,
    elevation: Dp = 10.dp,
    innerPadding: PaddingValues = PaddingValues(12.dp, 5.dp),
) {
    BasicTextField(
        modifier = modifier,
        value = stateSearchItem.value,
        onValueChange =  {stateSearchItem.value =  it},
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = onSearchKeyboard),
        singleLine = true,
        maxLines = 1,
        textStyle = TextStyle(color = MaterialTheme.colors.onPrimary),
        cursorBrush = SolidColor(Color.White),
        decorationBox={
            Surface(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                elevation = elevation,
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colors.primary
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                ) {
                    if(stateSearchItem.value.isBlank()) {
                        Text(
                            text = "Search",
                            style = TextStyle(color = MaterialTheme.colors.onPrimary.copy(alpha = .6f))
                        )
                    }
                    it()
                }
            }
        }
    )
}

@Composable
fun SearchButton(
    onSearch: () -> Unit,
    iconColor: Color
) {
    IconButton(onClick = onSearch) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = "search item",
            tint = iconColor
        )
    }
}

@Composable
fun FilterDate(
    onFilter: () -> Unit,
    scaffoldState: ScaffoldState,
    thread: CoroutineScope,
    focus: FocusManager,
    dateStartChange: MutableState<Date>,
    dateEndChange: MutableState<Date>,
) {
//    ModalDrawer(
//        drawerState = drawerState,
//        gesturesEnabled = false,
//        drawerContent = {
    Text(text = "Filter: ", style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
    Text(text = "from: ", style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
        val startAttr:MutableState<TextFieldValue> = remember { mutableStateOf(TextFieldValue()) }
    OutlinedTextField(
        modifier = Modifier.padding(10.dp),
        value = startAttr.value,
        onValueChange = {
            if (it.text.last().isDigit()) {
                if (it.text.length <= 10) {
                    if (it.text.matches(Regex("[1-9][0-9]{3}/(0[0-9]|1[0-2])/([0-2][0-9]|3[01])"))) {
                        dateStartChange.value = SimpleDateFormat("yyyy/MM/dd", Locale.US).parse(it.text)!!
                    }
                    if (it.text.length == 5) {
                        if (it.text[4] != '/')
                            startAttr.value = TextFieldValue(it.text.substring(0,4)+"/"+it.text[4], selection = TextRange(it.text.length+1))
                        else
                            startAttr.value = TextFieldValue(it.text, selection = TextRange(it.text.length))
                    } else if (it.text.length == 8) {
                        if (it.text[7] != '/')
                            startAttr.value = TextFieldValue(it.text.substring(0,7)+"/"+it.text[7], selection = TextRange(it.text.length+1))
                        else
                            startAttr.value = TextFieldValue(it.text, selection = TextRange(it.text.length))
                    } else
                        startAttr.value = TextFieldValue(it.text, selection = TextRange(it.text.length))
                }
            } else
                it.text.removeRange(it.text.lastIndex,it.text.lastIndex+1)},
        leadingIcon = { Icon(imageVector = Icons.Rounded.DateRange, contentDescription = null) },
        label = {Text(text = "change date value (yyyy/MM/dd)", style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)},
        placeholder = { Text(text = "exp: 2000/02/04", style = MaterialTheme.typography.h6) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = {
            focus.moveFocus(FocusDirection.Down)
        })
    )
    Text(text = "change date value", style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)
    Text(text = " to ", style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
    val endAttr:MutableState<TextFieldValue> = remember { mutableStateOf(TextFieldValue()) }
    OutlinedTextField(
        modifier = Modifier.padding(10.dp),
        value = endAttr.value,
        onValueChange = {
            if (it.text.last().isDigit()) {
                if (it.text.length <= 10) {
                    if (it.text.matches(Regex("[1-9][0-9]{3}/(0[0-9]|1[0-2])/([0-2][0-9]|3[01])"))) {
                        dateEndChange.value =
                            SimpleDateFormat("yyyy/MM/dd", Locale.US).parse(it.text)!!
                    }
                    if (it.text.length == 5) {
                        if (it.text[4] != '/')
                            endAttr.value = TextFieldValue(
                                it.text.substring(0, 4) + "/" + it.text[4],
                                selection = TextRange(it.text.length + 1)
                            )
                        else
                            endAttr.value =
                                TextFieldValue(it.text, selection = TextRange(it.text.length))
                    } else if (it.text.length == 8) {
                        if (it.text[7] != '/')
                            endAttr.value = TextFieldValue(
                                it.text.substring(0, 7) + "/" + it.text[7],
                                selection = TextRange(it.text.length + 1)
                            )
                        else
                            endAttr.value =
                                TextFieldValue(it.text, selection = TextRange(it.text.length))
                    } else
                        endAttr.value =
                            TextFieldValue(it.text, selection = TextRange(it.text.length))
                }
            } else
                it.text.removeRange(it.text.lastIndex,it.text.lastIndex+1)},
        leadingIcon = { Icon(imageVector = Icons.Rounded.DateRange, contentDescription = null) },
        label = {Text(text = "change date value (yyyy/MM/dd)", style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)},
        placeholder = { Text(text = "exp: 2000/02/04", style = MaterialTheme.typography.h6) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = {
            focus.moveFocus(FocusDirection.Up)
        })
    )
    Column(horizontalAlignment = Alignment.End) {
        Spacer(modifier = Modifier
            .fillMaxHeight()
            .weight(1f))
        Button(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 30.dp)
                .padding(10.dp),
            onClick = {
                thread.launch {
                    scaffoldState.drawerState.close()
                }
                focus.clearFocus()
                onFilter()
            }
        ) {
            Text(text = "Done")
        }
    }
//        }
//    ) {}
}

@Composable
fun FilterBigDecimal(
    onFilter: () -> Unit,
    scaffoldState: ScaffoldState,
    thread: CoroutineScope,
    focus: FocusManager,
    costStartChange: MutableState<BigDecimal>,
    costEndChange: MutableState<BigDecimal>
) {
//    ModalDrawer(
//        drawerState = drawerState,
//        gesturesEnabled = false,
//        drawerContent = {
    Text(text = "Range Cost: ", style = MaterialTheme.typography.h6, fontStyle = FontStyle.Italic)
    OutlinedTextField(
        modifier = Modifier.padding(10.dp),
        value = TextFieldValue(DecimalFormat("#,###").format(costStartChange.value), selection = TextRange(DecimalFormat("#,###").format(costStartChange.value).length)),
        onValueChange = {
            if (it.text.last().isDigit()) {
                costStartChange.value = if (it.text.isNotBlank()) it.text.replace(Regex("[,]*"), "")
                    .toBigDecimal() else BigDecimal.ZERO
            }},
        label = {Text(text = "start cost value", style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)},
        leadingIcon = { Text(text = "Rp. ",style = MaterialTheme.typography.h6) },
        trailingIcon = { Text(text = ".00",style = MaterialTheme.typography.h6) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = {
            focus.moveFocus(FocusDirection.Down)
        })
    )
    OutlinedTextField(
        modifier = Modifier.padding(10.dp),
        value = TextFieldValue(DecimalFormat("#,###").format(costEndChange.value), selection = TextRange(DecimalFormat("#,###").format(costEndChange.value).length)),
        onValueChange = {
            if (it.text.last().isDigit()) {
                costEndChange.value = if (it.text.isNotBlank()) it.text.replace(Regex("[,]*"),"").toBigDecimal() else BigDecimal.ZERO
            }},
        leadingIcon = { Text(text = "Rp. ",style = MaterialTheme.typography.h6) },
        trailingIcon = { Text(text = ".00",style = MaterialTheme.typography.h6) },
        label = {Text(text = "end cost value", style = MaterialTheme.typography.caption, fontStyle = FontStyle.Italic)},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = {
            focus.moveFocus(FocusDirection.Up)
        })
    )
    Column(horizontalAlignment = Alignment.End) {
        Spacer(modifier = Modifier
            .fillMaxHeight()
            .weight(1f))
        Button(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 30.dp)
                .padding(10.dp),
            onClick = {
                thread.launch {
                    scaffoldState.drawerState.close()
                }
                focus.clearFocus()
                onFilter()
            }
        ) {
            Text(text = "Done")
        }
    }
//        }
//    ){}
}

data class MessageItem(
    val label:String = "",
    val description: String = "",
    val error: Boolean = false
)

@Composable
fun MessageDialog(
    title: String,
    doneText: String = "Done",
    onDone: () -> Unit,
    button1Color: Color = MaterialTheme.colors.onBackground,
    button2Color: Color = MaterialTheme.colors.onBackground,
    error: Boolean?,
    closeText: String = "Close",
    withCloseButton: Boolean = true,
    open: MutableState<Boolean>,
    onClose: () -> Unit = { open.value = !open.value },
    onDismiss: () -> Unit = {},
    body: String
) {
    if (open.value) {
        AlertDialog(
            modifier = Modifier.padding(40.dp,0.dp).padding(),
            backgroundColor = if (error != null) {if (error) Color.Red else Color(0xFF03C988)} else MaterialTheme.colors.background,
            contentColor = if (error != null) {Color.White} else Color(0xFF82AAE3),
            onDismissRequest = onDismiss,
            title = {
                Text(text = title, style = MaterialTheme.typography.h3)
            },
            text = {
                Text(text = body, style = MaterialTheme.typography.body1)
            },
            buttons = {
                Row(
                    modifier = Modifier
                        .padding(all = 5.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        modifier = Modifier
                            .padding(5.dp),
                        onClick = onDone,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = if (error != null) {Color.White} else button1Color
                        )
                    ) {
                        Text(text = doneText)
                    }
                    if (withCloseButton) {
                        TextButton(
                            modifier = Modifier
                                .padding(10.dp, 0.dp, 0.dp, 0.dp)
                                .padding(5.dp),
                            onClick = onClose,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Transparent,
                                contentColor = if (error != null) {Color.White} else button2Color
                            )
                        ) {
                            Text(text = closeText)
                        }
                    }
                }
            }
        )
    }
}

//@Composable
//fun Pagination (
//    disableNextState: MutableState<Boolean> = remember { mutableStateOf(false) },
//    disablePrevState: MutableState<Boolean> = remember { mutableStateOf(false) },
//    statePage: MutableState<Int>,
//    dataOut: SnapshotStateList<Any>,
//    dataIn: List<Any>
//) {
//    Row (verticalAlignment = Alignment.CenterVertically) {
//        PaginationButton(
//            disableState = disablePrevState,
//            statePage = statePage,
//            dataOut = dataOut,
//            dataIn = dataIn,
//            isNextButton = false
//        )
//        Spacer(modifier = Modifier
//            .fillMaxWidth()
//            .weight(1f))
//        PaginationButton(
//            disableState = disableNextState,
//            statePage = statePage,
//            dataOut = dataOut,
//            dataIn = dataIn,
//            isNextButton = true
//        )
//    }
//}
//
//@Composable
//private fun PaginationButton (
//    disableState: MutableState<Boolean>,
//    statePage: MutableState<Int>,
//    dataOut: SnapshotStateList<Any>,
//    dataIn: List<Any>,
//    isNextButton: Boolean,
//){
//    IconButton(
//        modifier = Modifier
//            .padding(20.dp, 0.dp)
//            .padding(0.dp),
//        enabled = disableState.value,
//        onClick = {
//            if (isNextButton) {
//                if(statePage.value+1 <= dataOut.size) {
//                    if (statePage.value+2 > dataOut.size) {
//                        disableState.value = !disableState.value
//                    }
//                    statePage.value += 1
//                    dataOut.removeRange(0,dataOut.size)
//                    dataOut.addAll(dataIn)
//                }
//            } else {
//                if(statePage.value-1 >= 1) {
//                    if (statePage.value-2 < 1) {
//                        disableState.value = !disableState.value
//                    }
//                    statePage.value -= 1
//                    dataOut.removeRange(0,dataOut.size)
//                    dataOut.addAll(dataIn)
//                }
//            }
//        }
//    ) {
//        Icon(imageVector = Icons.Outlined.FirstPage, contentDescription = "previous")
//    }
//}

fun colorGenerate(): SnapshotStateMap<Char,Color> {
    val colors: SnapshotStateMap<Char, Color> = mutableStateMapOf()
    val red = setOf(0xFFFF0032,0xFFFF8B13,0xFFFF7B54,0xFFFFB26B,0xFFFFD56F,0xFFFFD495,0xFFFF6E31,0xFFF1F7B5,0xFFFEC868)
    val green = setOf(0xFFABC270,0xFF03C988,0xFF4E6C50,0xFF939B62,0xFF61876E,0xFF3C6255,0xFFA6BB8D,0xFF227C70)
    val blue = setOf(0xFF00337C,0xFF1C82AD,0xFF0081B4,0xFF8DCBE6,0xFFA0C3D2,0xFF6C00FF,0xFF5BC0F8,0xFF86C8BC)
    for (i in 1..25) {
        if (i <= 9)
            colors[(64+i).toChar()] = Color(red.elementAt(i-1))
        else if (i <= 17)
            colors[(64+i).toChar()] = Color(green.elementAt(i-9-1))
        else
            colors[(64+i).toChar()] = Color(blue.elementAt(i-17-1))
    }
    return colors
}

//@Composable
//fun <T:Any> rememberSaveableListOf(vararg data: T): SnapshotStateList<T> {
//    return rememberSaveable(
//        saver = listSaver(
//            save = {
//                if (it.isNotEmpty())
//                    if (!canBeSaved(it.first()))
//                        throw IllegalStateException("${it.first()::class} cannot be saved. By default only types which can be stored in the Bundle class can be saved.")
//                it.toList()
//            },
//            restore = { it.toMutableStateList() }
//        )
//    ) {
//        data.toList().toMutableStateList()
//    }
//}

@Suppress("UNCHECKED_CAST")
class TextFieldCustom<T>(
    val value: MutableState<T>,
    val modifier: Modifier,
    val onNext: KeyboardActionScope.() -> Unit,
    val singleLine: Boolean = true,
    val label: String,
    val maxLines: Int = 1,
    private val rangeNumber: Long = -1,
    val textStyle: TextStyle = TextStyle(),
    val placeholder: String = "",
    private val currencySymbol: String = "Rp",
    val currencySymbolIcon: ImageVector? = null,
    val endSymbol: String? = null,
    val color: Color,
    val counterChar: Int? = null,
    private val counter: MutableState<Int>? = null,
    val additionalInfo: String? = null,
    private val innerPadding: PaddingValues = PaddingValues(12.dp, 5.dp)
) {
    private val valueIns: (it: T) -> TextFieldValue
    private val valueChange: (it: TextFieldValue) -> Unit
    init {
        if (counterChar != null)
            if (counter == null)
                throw RuntimeException("variable counter cannot be null if using counter Char !!!")
            else
                counter.value = counterChar
        valueIns = when (value.value) {
            is String -> {
                { TextFieldValue(it as String, selection = TextRange(it.length)) }
            }
            is BigDecimal -> {
                { TextFieldValue(DecimalFormat("#,###").format(it), selection = TextRange(DecimalFormat("#,###").format(it).length)) }
            }
            else -> {
                { TextFieldValue(it.toString(), selection = TextRange(it.toString().length)) }
            }
        }
        valueChange = when(value.value) {
            is String -> {{
                if (counterChar != null) {
                    val length = it.text.length
                    if (length <= counterChar) {
                        counter?.value = counterChar - it.text.length
                    }
                }
                value.value = it.text as T
            }}
            is BigDecimal -> {{
                if (it.text.isNotBlank()) {
                    if (it.text.last().isDigit()) {
                        it.text.replace(Regex(",*"),"").toBigDecimal().let {itd ->
                            if (rangeNumber >= 0) {
                                if (itd.compareTo(BigDecimal.valueOf(rangeNumber)) < 1)
                                    value.value = itd as T
                            }
                            else
                                value.value = itd as T
                        }
                    }
                } else
                    value.value = BigDecimal.ZERO as T
            }}
            else -> {{
                if (it.text.isNotBlank()) {
                    if (it.text.last().isDigit()) {
                        value.value = it.text.toInt() as T
                    }
                } else
                    value.value = 0 as T
            }}
        }
    }

    @Composable
    fun Build() {
        BasicTextField(
            modifier = modifier,
            value = valueIns(value.value),
            onValueChange = valueChange,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = when(value.value) {
                    is String -> KeyboardType.Text
                    else -> KeyboardType.Number
                }
            ),
            keyboardActions = KeyboardActions(onNext = onNext),
            singleLine = singleLine,
            cursorBrush = SolidColor(color),
            maxLines = maxLines,
            textStyle = textStyle.copy(color = color, fontSize = (textStyle.fontSize.value + 2f).sp),
            decorationBox={
                Surface(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(if (maxLines == 1) 50 else 10),
                    color = MaterialTheme.colors.onBackground.copy(alpha = .2f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding)
                    ) {
                        Text(
                            modifier = Modifier.padding(),
                            text = label,
                            color = color,
                            style = MaterialTheme.typography.h6
                        )
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            if (currencySymbolIcon != null)
                                Icon(
                                    imageVector = currencySymbolIcon,
                                    contentDescription = null,
                                    tint = color
                                )
                            if (value.value is BigDecimal)
                                Text(text = "${currencySymbol}. ", style = textStyle.copy(color = color))
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)) {
                                if (value.value is String)
                                    if((value.value as String).isBlank()) {
                                        Text(
                                            text = placeholder,
                                            style = textStyle.copy(color = color.copy(alpha = .6f))
                                        )
                                    }
                                it()
                            }
                            if (value.value is BigDecimal)
                                Text(text = " .00", style = textStyle.copy(color = color))
                            else if(value.value is Int)
                                Text(text = endSymbol ?: "", style = textStyle.copy(color = color))
                        }
                        if (additionalInfo != null)
                            Text(
                                text = additionalInfo,
                                color = color,
                                style = MaterialTheme.typography.body1
                            )
                        if (counter != null)
                            Text(
                                text = "remaining character: ${counter.value}",
                                color = color,
                                style = MaterialTheme.typography.body1
                            )
                    }
                }
            }
        )
    }
}

fun clearItem(
    name: MutableState<String>,
    buyCost: MutableState<BigDecimal>,
    sellCost: MutableState<BigDecimal>,
    discount: MutableState<Int>,
    sold_out: MutableState<Int>,
    buyed: MutableState<Int>,
) {
    name.value = ""
    buyCost.value = BigDecimal.ZERO
    sellCost.value = BigDecimal.ZERO
    discount.value = 0
    sold_out.value = 0
    buyed.value = 0
}

fun clearNote(
    title: MutableState<String>,
    description: MutableState<String>,
    income: MutableState<BigDecimal>,
    outcome: MutableState<BigDecimal>,
    additionalOutcome: MutableState<BigDecimal>,
    itemsList: SnapshotStateList<ItemRaw>
) {
    title.value = ""
    description.value = ""
    income.value = BigDecimal.ZERO
    outcome.value = BigDecimal.ZERO
    additionalOutcome.value = BigDecimal.ZERO
    itemsList.removeRange(0,itemsList.size)
}

