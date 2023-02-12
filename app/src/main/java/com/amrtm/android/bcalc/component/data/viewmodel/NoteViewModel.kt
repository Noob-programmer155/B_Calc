package com.amrtm.android.bcalc.component.data.viewmodel

import androidx.compose.runtime.*
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
class NoteViewModel(
    private val noteRepo: NoteRepo,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        private val START_DATE = Date(0)
        private val END_DATE = Date(Date().time+86400000)
        private const val DEFAULT_QUERY = ""
    }

    val noteId: String? = savedStateHandle["id"]

    private val pageSize: Int = (savedStateHandle["page"] ?: 10)/2

    val state: StateFlow<State.StateNote>
    val pagingData: Flow<PagingData<Note>>
    private val action: (Action) -> Unit

    fun onTrigger() {
        action(Action.Search(DEFAULT_QUERY))
        action(Action.Filter(START_DATE, END_DATE))
    }

    fun onSearch(query: String){
        action(Action.Search(query))
    }

    fun onFilter(start: Date, end: Date) {
        action(Action.Filter(start, end))
    }

    init {
        val initSearch = savedStateHandle[SAVED_NAME_QUERY] ?: DEFAULT_QUERY
        val initStartDate = savedStateHandle[SAVED_NAME_START_DATE] ?: START_DATE
        val initEndDate = savedStateHandle[SAVED_NAME_END_DATE] ?: END_DATE
        val sharedFlow = MutableSharedFlow<Action>()
        val search = sharedFlow.filterIsInstance<Action.Search>()
            .distinctUntilChanged()
            .onStart { emit(Action.Search(query = initSearch)) }
        val filter = sharedFlow.filterIsInstance<Action.Filter>()
            .distinctUntilChanged()
            .onStart { emit(Action.Filter(initStartDate,initEndDate)) }

        pagingData = combine(search,filter,::Pair).flatMapLatest {
            searchNote(it.first.query,it.second.start,it.second.end)
        }.cachedIn(viewModelScope)

        state = combine(search,filter,::Pair). map{
            State.StateNote(
                search = it.first.query,
                dateStart = it.second.start,
                dateEnd = it.second.end
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = State.StateNote(DEFAULT_QUERY, START_DATE, END_DATE)
        )

        action = {action ->
            viewModelScope.launch { sharedFlow.emit(action) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        savedStateHandle[SAVED_NAME_QUERY] = state.value.search
        savedStateHandle[SAVED_NAME_START_DATE] = state.value.dateStart
        savedStateHandle[SAVED_NAME_END_DATE] = state.value.dateEnd
    }

    val noteData: StateFlow<NoteItems> = noteRepo.get(noteId?.toLongOrNull())
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = NoteItems(
                Note(0,Date(),"","", BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO), listOf()
            )
        )

    private fun searchNote(query: String, start: Date, end: Date): Flow<PagingData<Note>> = noteRepo.getAll(query,pageSize, start, end)

    suspend fun add(note: Note): Long {
        return noteRepo.add(note)
    }
    fun update(note: Note) {
        viewModelScope.launch {
            noteRepo.update(note)
        }
    }
    fun delete(id: Long) {
        noteRepo.delete(IdRef(id))
    }
}

sealed interface State {
    data class StateItem(
        val search: String,
        val costStart: BigDecimal,
        val costEnd: BigDecimal
    ): State
    data class StateNote(
        val search: String,
        val dateStart: Date,
        val dateEnd: Date
    ): State
}

sealed class Action {
    data class Search(val query: String): Action()
    data class SearchItem(val query: String): Action()
    data class Filter(val start: Date, val end: Date): Action()
    data class FilterItem(val start: BigDecimal, val end: BigDecimal): Action()
    data class QueryItem(val name: String): Action()
}

private const val SAVED_NAME_QUERY: String = "saved_query_search_home"
private const val SAVED_NAME_START_DATE: String = "saved_start_date_filter_home"
private const val SAVED_NAME_END_DATE: String = "saved_end_date_filter_home"