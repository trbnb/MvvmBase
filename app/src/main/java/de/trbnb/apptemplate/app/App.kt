package de.trbnb.apptemplate.app

import android.app.Application
import android.content.Context
import de.trbnb.apptemplate.BR
import de.trbnb.mvvmbase.bindableproperty.BindableProperty

class App : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        BindableProperty.init<BR>()
    }

}

val Context.appComponent: AppComponent
    get() = (applicationContext as? App)?.appComponent ?: throw IllegalStateException("Application context must be App.")
