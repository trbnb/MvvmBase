package de.trbnb.apptemplate.app

import dagger.Component
import de.trbnb.apptemplate.main.MainActivity
import de.trbnb.apptemplate.second.SecondViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ResourceModule::class, ViewModelModule::class])
interface AppComponent {
    // ViewModels
    val secondViewModelFactory: SecondViewModel.Factory

    fun inject(mainActivity: MainActivity)
}
