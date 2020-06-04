package de.trbnb.apptemplate.app

import dagger.Component
import de.trbnb.apptemplate.main.MainViewModel
import de.trbnb.apptemplate.second.SecondViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ResourceModule::class, ViewModelModule::class])
interface AppComponent {
    // ViewModels
    val mainViewModelFactory: MainViewModel.Factory
    val secondViewModelFactory: SecondViewModel.Factory
}
