package de.trbnb.apptemplate.app

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Suppress("UndocumentedPublicClass")
@Module
class AppModule(private val app: App) {
    @Provides
    @Singleton
    @Suppress("UndocumentedPublicFunction")
    fun context(): Context = app
}
