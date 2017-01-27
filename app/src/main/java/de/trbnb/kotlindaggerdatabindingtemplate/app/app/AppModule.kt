package de.trbnb.kotlindaggerdatabindingtemplate.app.app

import android.content.Context
import dagger.Module
import dagger.Provides
import de.trbnb.kotlindaggerdatabindingtemplate.app.app.App
import javax.inject.Singleton

@Module
class AppModule(private val app: App) {

    @Provides
    @Singleton
    fun context(): Context = app
}