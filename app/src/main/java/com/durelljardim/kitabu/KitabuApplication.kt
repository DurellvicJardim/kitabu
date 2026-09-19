package com.durelljardim.kitabu

import android.app.Application
import com.durelljardim.kitabu.di.AppContainer

class KitabuApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
