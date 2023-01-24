package com.amrtm.android.bcalc.component.data.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrtm.android.bcalc.component.data.repository.Balance
import com.amrtm.android.bcalc.component.data.repository.StatusBalance
import com.amrtm.android.bcalc.component.data.repository.BalanceRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

class HomeViewModel(private val balanceRepo: BalanceRepo): ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    var collapseCard = mutableStateOf(-2)
        private set

    fun setCollapseCard(state: Int) {
        this.collapseCard.value = state
    }

    val balanceData: StateFlow<Balance> = balanceRepo.get().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = Balance(0,"myBalance",BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
            Date(),StatusBalance.Balance,0,0)
    )

    fun add(balance: Balance) {
        viewModelScope.launch {
            balanceRepo.add(balance)
        }
    }

    fun update(balance: Balance) {
        viewModelScope.launch {
            balanceRepo.update(balance)
        }
    }
}