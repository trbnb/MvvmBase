package de.trbnb.apptemplate.app

import android.content.Context
import dagger.Module
import dagger.Provides
import de.trbnb.apptemplate.app.App
import javax.inject.Singleton

@Module
class AppModule(private val app: App) {

    @Provides
    @Singleton
    fun context(): Context = app
}