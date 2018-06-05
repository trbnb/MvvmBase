package de.trbnb.apptemplate.app

import android.app.Application
import de.trbnb.apptemplate.BR
import de.trbnb.mvvmbase.bindableproperty.BindableProperty

class App : Application(){

    companion object{
        lateinit var appComponent: AppComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()

        BindableProperty.init<BR>()

        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }

}
