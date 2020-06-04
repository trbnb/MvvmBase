package de.trbnb.apptemplate.app

import dagger.Component
import de.trbnb.apptemplate.main.MainViewModel
import de.trbnb.apptemplate.second.SecondViewModel
import javax.inject.Singleton

@Suppress("UndocumentedPublicClass")
@Singleton
@Component(modules = [AppModule::class, ResourceModule::class, ViewModelModule::class])
interface AppComponent {
    // ViewModels
    @Suppress("UndocumentedPublicProperty")
    val mainViewModelFactory: MainViewModel.Factory
    @Suppress("UndocumentedPublicProperty")
    val secondViewModelFactory: SecondViewModel.Factory
}
