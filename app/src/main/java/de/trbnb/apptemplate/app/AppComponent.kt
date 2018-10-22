package de.trbnb.apptemplate.app

import dagger.Component
import de.trbnb.apptemplate.main.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    // ViewModels
    fun inject(mainActivity: MainActivity)
}
