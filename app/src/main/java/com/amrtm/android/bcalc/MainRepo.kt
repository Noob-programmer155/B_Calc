package com.amrtm.android.bcalc

import android.app.Application
import com.amrtm.android.bcalc.component.data.repository.MainContainerImpl

class MainRepo (): Application() {
    lateinit var container: MainContainerImpl
    override fun onCreate() {
        super.onCreate()
        container = MainContainerImpl(this)
    }
}