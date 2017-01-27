package de.trbnb.apptemplate.main

import dagger.Module
import dagger.Provides
import de.trbnb.base.dagger.ActivityScope

@Module
class MainModule() {

    @ActivityScope
    @Provides
    fun getViewModel() = MainViewModel()

}