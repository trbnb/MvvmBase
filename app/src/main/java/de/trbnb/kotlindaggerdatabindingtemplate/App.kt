package de.trbnb.kotlindaggerdatabindingtemplate

import android.app.Application

class App : Application(){

    lateinit var component: Component
        private set

    override fun onCreate() {
        super.onCreate()

        app = this

        component = object : Component {
            override fun inject() {

            }
        }
                /*DaggerComponent
                .builder()
                .netModule(NetModule())
                .build()*/
    }

    companion object {

        lateinit var app: App
            private set

        val component: Component
            get() = app.component
    }

}

fun inject() = App.component.inject()