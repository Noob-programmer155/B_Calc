package com.amrtm.android.bcalc.component.data.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.amrtm.android.bcalc.component.data.repository.Item
import com.amrtm.android.bcalc.component.data.repository.ItemHistory
import com.amrtm.android.bcalc.component.data.repository.ItemRepo
import com.amrtm.android.bcalc.component.data.repository.NoteRepo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal

class VisualViewModel(private val itemRepo: ItemRepo, savedStateHandle: SavedStateHandle): ItemViewModel(itemRepo,savedStateHandle) {
    private val name: MutableState<String?> = mutableStateOf(null)

    fun setName(name: String) {
        this.name.value = name
    }

    fun getListItems():Flow<PagingData<Item>> = itemRepo.getAllItems(pageSize = pageSize)

    fun itemDataVisual(): Flow<List<ItemHistory>> = itemRepo.get(name.value!!)
}