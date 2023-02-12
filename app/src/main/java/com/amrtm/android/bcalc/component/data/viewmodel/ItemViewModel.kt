package com.amrtm.android.bcalc.component.data.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.amrtm.android.bcalc.component.data.repository.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
open class ItemViewModel(
    private val itemRepo: ItemRepo,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        private val START_NUMBER = BigDecimal.ZERO
        private val END_NUMBER = BigDecimal.valueOf(Long.MAX_VALUE)
        private const val DEFAULT_QUERY = ""
    }

    val listItemRaw: SnapshotStateList<ItemRaw> = mutableStateListOf()

    val pageSize: Int = (savedStateHandle["page"] ?: 10)

    private val state: StateFlow<State.StateItem>
    val pagingData: Flow<PagingData<Item>>
    val stock: StateFlow<Int?>

    private val action: (Action) -> Unit

    fun onTrigger() {
        action(Action.SearchItem(DEFAULT_QUERY))
        action(Action.FilterItem(START_NUMBER, END_NUMBER))
    }

    fun onStock(name: String) {
        action(Action.QueryItem(name))
    }

    fun onSearch(query: String){
        action(Action.SearchItem(query.uppercase()))
    }

    fun onFilter(start: BigDecimal, end: BigDecimal) {
        action(Action.FilterItem(start, end))
    }

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
        val stockTrigger = sharedFlow.filterIsInstance<Action.QueryItem>()

        stock = stockTrigger.flatMapLatest {
            getStock(it.name)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = null
        )

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
    }

    override fun onCleared() {
        super.onCleared()
        savedStateHandle[SAVED_NAME_QUERY_ITEM] = state.value
        savedStateHandle[SAVED_NAME_START_NUMBER] = state.value.costStart
        savedStateHandle[SAVED_NAME_END_NUMBER] = state.value.costEnd
    }

    private fun searchItem(query: String, start: BigDecimal, end: BigDecimal): Flow<PagingData<Item>> = itemRepo.getAllItems(query,pageSize,start,end)

    fun getStock(name:String):  Flow<Int?> = itemRepo.getStock(name)

    fun getCountItems(): Flow<Int> = itemRepo.getCount()

    suspend fun addAll(item: List<ItemHistory>) {
        itemRepo.addAll(item)
    }

    suspend fun deleteAll(ids: List<ItemHistory>) {
        itemRepo.deleteAll(ids)
    }
}

private const val SAVED_NAME_QUERY_ITEM: String = "saved_query_search_item"
private const val SAVED_NAME_START_NUMBER: String = "saved_start_number_filter_item"
private const val SAVED_NAME_END_NUMBER: String = "saved_end_number_filter_item"