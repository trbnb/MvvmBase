package de.trbnb.apptemplate.app

import android.app.Application
import de.trbnb.apptemplate.BR
import de.trbnb.mvvmbase.MvvmBase

@Suppress("UndocumentedPublicProperty")
lateinit var appComponent: AppComponent
    private set

@Suppress("UndocumentedPublicClass")
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        MvvmBase.init<BR>()
    }
}
