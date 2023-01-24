package com.amrtm.android.bcalc;

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amrtm.android.bcalc.component.data.viewmodel.HomeViewModel
import com.amrtm.android.bcalc.component.data.viewmodel.ItemViewModel
import com.amrtm.android.bcalc.component.data.viewmodel.NoteViewModel

object ViewMain {
    val Factory = viewModelFactory{
        initializer {
            HomeViewModel(dataLoader().container.balanceRepo)
        }
        initializer {
            NoteViewModel(dataLoader().container.noteRepo,this.createSavedStateHandle())
        }
        initializer {
            ItemViewModel(dataLoader().container.itemRepo,this.createSavedStateHandle())
        }
    }
}

fun CreationExtras.dataLoader(): MainRepo = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainRepo)