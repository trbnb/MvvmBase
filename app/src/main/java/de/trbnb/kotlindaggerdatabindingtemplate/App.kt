package de.trbnb.kotlindaggerdatabindingtemplate

import android.app.Application

class App : Application(){

    lateinit var component: Component
        private set

    override fun onCreate() {
        super.onCreate()

        instance = this

        component = DaggerComponent     // Unsolved reference
                .builder()
                .netModule(NetModule())
                .build()
    }

    companion object {

        lateinit var instance: App
            private set
    }

}