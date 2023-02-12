package com.amrtm.android.bcalc

import android.app.Application
import com.amrtm.android.bcalc.component.data.repository.MainContainer
import com.amrtm.android.bcalc.component.data.repository.MainContainerImpl

class BCalc: Application() {
    lateinit var container: MainContainer
    override fun onCreate() {
        super.onCreate()
        container = MainContainerImpl(this)
    }
}