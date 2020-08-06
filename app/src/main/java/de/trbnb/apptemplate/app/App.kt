package de.trbnb.apptemplate.app

import android.app.Application
import de.trbnb.mvvmbase.MvvmBase

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        MvvmBase.autoInit()
    }
}
