package de.trbnb.apptemplate.app

import dagger.Component
import de.trbnb.apptemplate.main.MainViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    // ViewModels
    fun inject(mainViewModel: MainViewModel)
}