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

    val balanceData: StateFlow<Balance?> = balanceRepo.get().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = null
    )

    fun update(balance: Balance?) {
        viewModelScope.launch {
            balanceRepo.update(balance as Any)
        }
    }
}