package de.trbnb.apptemplate.app

import dagger.Component
import de.trbnb.apptemplate.app.AppModule
import de.trbnb.apptemplate.main.MainComponent
import de.trbnb.apptemplate.main.MainModule
import de.trbnb.apptemplate.main.MainViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    // ViewModels
    fun inject(mainViewModel: MainViewModel)

    // Subcomponents
    fun plus(mainModule: MainModule): MainComponent
}