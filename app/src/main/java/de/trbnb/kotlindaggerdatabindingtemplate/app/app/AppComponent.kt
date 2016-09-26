package de.trbnb.kotlindaggerdatabindingtemplate.app.app

import dagger.Component
import de.trbnb.kotlindaggerdatabindingtemplate.app.app.AppModule
import de.trbnb.kotlindaggerdatabindingtemplate.app.main.MainComponent
import de.trbnb.kotlindaggerdatabindingtemplate.app.main.MainModule
import de.trbnb.kotlindaggerdatabindingtemplate.app.main.MainViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    // ViewModels
    fun inject(mainViewModel: MainViewModel)

    // Subcomponents
    fun plus(mainModule: MainModule): MainComponent
}