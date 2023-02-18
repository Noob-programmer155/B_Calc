package com.amrtm.android.bcalc.component.data.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.amrtm.android.bcalc.component.data.VisualDataAttributeLoader
import com.amrtm.android.bcalc.component.data.repository.*
import com.amrtm.android.bcalc.component.view.Visualization
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
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

    private val _state: MutableStateFlow<State.StateItem>
    val pagingData: Flow<PagingData<Item>>
    private val _dataVisual: MutableStateFlow<State.StateVisual>
    val dataVisual: Flow<List<ItemHistory>>

    fun clearQuery() {
        _state.value = State.StateItem(DEFAULT_QUERY, START_NUMBER, END_NUMBER)
    }

    fun onSearch(query: String) {
        _state.value = _state.value.copy(search = query)
    }

    fun onFilter(start: BigDecimal, end: BigDecimal) {
        _state.value = _state.value.copy(costStart = start, costEnd = end)
    }

    fun setName(name: String?) {
        _dataVisual.value = _dataVisual.value.copy(name = name ?: "")
    }

    init {
        val initSearch = savedStateHandle[SAVED_NAME_QUERY_ITEM] ?: DEFAULT_QUERY
        val initStart = savedStateHandle[SAVED_NAME_START_NUMBER] ?: START_NUMBER
        val initEnd = savedStateHandle[SAVED_NAME_END_NUMBER] ?: END_NUMBER
        _state = MutableStateFlow(State.StateItem(initSearch,initStart,initEnd))
        _dataVisual = MutableStateFlow(State.StateVisual(""))

        dataVisual = _dataVisual.flatMapLatest {
            itemDataVisual(it.name)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = listOf()
        )

        pagingData = _state.flatMapLatest {
            searchItem(it.search, it.costStart, it.costEnd)
        }.cachedIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        listItemRaw.clear()
        savedStateHandle[SAVED_NAME_QUERY_ITEM] = _state.value.search
        savedStateHandle[SAVED_NAME_START_NUMBER] = _state.value.costStart
        savedStateHandle[SAVED_NAME_END_NUMBER] = _state.value.costEnd
    }

    private fun itemDataVisual(name: String): Flow<List<ItemHistory>> = itemRepo.get(name)

    private fun searchItem(query: String, start: BigDecimal, end: BigDecimal): Flow<PagingData<Item>> = itemRepo.getAllItems(query,pageSize,start,end)

    suspend fun getStock(name:String):  Int? = itemRepo.getStock(name)

    suspend fun getCountItems(): Int = itemRepo.getCount()

    suspend fun addAll(item: List<ItemHistory>) {
        itemRepo.addAll(item)
    }

    suspend fun deleteAll(items: List<ItemHistory>) {
        itemRepo.deleteAll(items)
    }
}

private const val SAVED_NAME_QUERY_ITEM: String = "saved_query_search_item"
private const val SAVED_NAME_START_NUMBER: String = "saved_start_number_filter_item"
private const val SAVED_NAME_END_NUMBER: String = "saved_end_number_filter_item"