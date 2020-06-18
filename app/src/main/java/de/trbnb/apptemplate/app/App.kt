package de.trbnb.apptemplate.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import de.trbnb.apptemplate.BR
import de.trbnb.mvvmbase.MvvmBase

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        MvvmBase.init<BR>()
    }
}
