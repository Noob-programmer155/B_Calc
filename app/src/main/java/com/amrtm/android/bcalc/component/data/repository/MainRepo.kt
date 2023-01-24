package com.amrtm.android.bcalc.component.data.repository

import android.content.Context

interface MainContainer {
    val balanceRepo: BalanceRepo
    val noteRepo: NoteRepo
    val itemRepo: ItemRepo
}

class MainContainerImpl(private val context: Context):MainContainer {
    val init = Database.getDatabase(context)
    override val balanceRepo: BalanceRepo by lazy {
        BalanceRepo(init.daoBalance())
    }
    override val noteRepo: NoteRepo by lazy {
        NoteRepo(init.daoNote())
    }
    override val itemRepo: ItemRepo by lazy {
        ItemRepo(init.daoItem())
    }
}