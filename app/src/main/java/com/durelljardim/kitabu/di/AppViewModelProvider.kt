package com.durelljardim.kitabu.di

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.durelljardim.kitabu.KitabuApplication
import com.durelljardim.kitabu.ui.LibraryViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            val application = this[APPLICATION_KEY] as KitabuApplication
            LibraryViewModel(application.container.repository)
        }
    }
}
