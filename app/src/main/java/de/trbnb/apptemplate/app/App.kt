package de.trbnb.apptemplate.app

import android.app.Application
import de.trbnb.apptemplate.BR
import de.trbnb.mvvmbase.MvvmBase

lateinit var appComponent: AppComponent
    private set

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        MvvmBase.init<BR>()
    }
}
