package com.amrtm.android.bcalc.component.data.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.amrtm.android.bcalc.component.data.repository.Item
import com.amrtm.android.bcalc.component.data.repository.ItemRepo
import com.amrtm.android.bcalc.component.data.repository.Note
import com.amrtm.android.bcalc.component.data.repository.NoteRepo
import com.amrtm.android.bcalc.component.view.colorGenerate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class ItemViewModel(
    private val itemRepo: ItemRepo,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        private val START_NUMBER = BigDecimal.ZERO
        private val END_NUMBER = BigDecimal.ZERO
        private const val DEFAULT_QUERY = ""
    }

    val pageSize: MutableState<Int> = mutableStateOf(10)

    val state: StateFlow<State.StateItem>
    val pagingData: Flow<PagingData<Item>>
    private val action: (Action) -> Unit

    fun onSearch(query: String){
        action(Action.SearchItem(query.uppercase()))
    }

    fun onFilter(start: BigDecimal, end: BigDecimal) {
        action(Action.FilterItem(start, end))
    }

    val colors: SnapshotStateMap<Char, Color> = mutableStateMapOf()

    init {
        val initSearch = savedStateHandle[SAVED_NAME_QUERY_ITEM] ?: DEFAULT_QUERY
        val initStart = savedStateHandle[SAVED_NAME_START_NUMBER] ?: START_NUMBER
        val initEnd = savedStateHandle[SAVED_NAME_END_NUMBER] ?: END_NUMBER
        val sharedFlow = MutableSharedFlow<Action>()
        val search = sharedFlow.filterIsInstance<Action.SearchItem>()
            .distinctUntilChanged()
            .onStart { emit(Action.SearchItem(query = initSearch)) }
        val filter = sharedFlow.filterIsInstance<Action.FilterItem>()
            .distinctUntilChanged()
            .onStart { emit(Action.FilterItem(initStart,initEnd)) }

        pagingData = combine(search,filter,::Pair).flatMapLatest {
            searchItem(it.first.query,it.second.start,it.second.end)
        }.cachedIn(viewModelScope)

        state = combine(search,filter,::Pair).map {
            State.StateItem(
                search = it.first.query,
                costStart = it.second.start,
                costEnd = it.second.end
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = State.StateItem(DEFAULT_QUERY, START_NUMBER, END_NUMBER)
        )

        action = {action ->
            viewModelScope.launch { sharedFlow.emit(action) }
        }

        colors.putAll(colorGenerate().filter { (k,_) -> !colors.containsKey(k) })
    }

    override fun onCleared() {
        super.onCleared()
        savedStateHandle[SAVED_NAME_QUERY_ITEM] = state.value
        savedStateHandle[SAVED_NAME_START_NUMBER] = state.value.costStart
        savedStateHandle[SAVED_NAME_END_NUMBER] = state.value.costEnd
    }

    suspend fun itemDataVisual(name: String): StateFlow<List<Item>> = itemRepo.get(name).stateIn(viewModelScope)

    fun searchItem(query: String, start: BigDecimal, end: BigDecimal): Flow<PagingData<Item>> = itemRepo.getAllItems(query,pageSize.value, start, end)

    suspend fun add(item: Item): Int {
        return itemRepo.add(item)
    }
    suspend fun addAll(item: List<Item>): List<Int> {
        return itemRepo.add(item)
    }
    fun update(item: Item) {
        viewModelScope.launch {
            itemRepo.update(item)
        }
    }
    fun updateAll(item: List<Item>) {
        viewModelScope.launch {
            itemRepo.update(item)
        }
    }
    fun delete(id: Int) {
        viewModelScope.launch {
            itemRepo.delete(id)
        }
    }
    fun deleteAll(ids: List<Int>) {
        viewModelScope.launch {
            itemRepo.delete(ids)
        }
    }
}

private const val SAVED_NAME_QUERY_ITEM: String = "saved_query_search_item"
private const val SAVED_NAME_START_NUMBER: String = "saved_start_number_filter_item"
private const val SAVED_NAME_END_NUMBER: String = "saved_end_number_filter_item"