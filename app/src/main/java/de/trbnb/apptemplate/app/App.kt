package de.trbnb.apptemplate.app

import android.app.Application

class App : Application(){

    companion object{
        lateinit var appComponent: AppComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }

}