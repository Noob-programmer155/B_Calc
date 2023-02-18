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

    private val _state: MutableStateFlow<State.StateNote>
    val pagingData: Flow<PagingData<Note>>

    fun clearQuery() {
        _state.value = State.StateNote(DEFAULT_QUERY, START_DATE, END_DATE)
    }

    fun onSearch(query: String){
        _state.value = _state.value.copy(search = query)
    }

    fun onFilter(start: Date, end: Date) {
        _state.value = _state.value.copy(dateStart = start, dateEnd = end)
    }

    init {
        val initSearch = savedStateHandle[SAVED_NAME_QUERY] ?: DEFAULT_QUERY
        val initStartDate = savedStateHandle[SAVED_NAME_START_DATE] ?: START_DATE
        val initEndDate = savedStateHandle[SAVED_NAME_END_DATE] ?: END_DATE
        _state = MutableStateFlow(State.StateNote(initSearch,initStartDate,initEndDate))

        pagingData = _state.flatMapLatest {
            searchNote(it.search,it.dateStart,it.dateEnd)
        }.cachedIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        savedStateHandle[SAVED_NAME_QUERY] = _state.value.search
        savedStateHandle[SAVED_NAME_START_DATE] = _state.value.dateStart
        savedStateHandle[SAVED_NAME_END_DATE] = _state.value.dateEnd
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
        viewModelScope.launch {
            noteRepo.delete(IdRef(id))
        }
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
    data class StateVisual(
        val name: String
    ): State
}

private const val SAVED_NAME_QUERY: String = "saved_query_search_home"
private const val SAVED_NAME_START_DATE: String = "saved_start_date_filter_home"
private const val SAVED_NAME_END_DATE: String = "saved_end_date_filter_home"