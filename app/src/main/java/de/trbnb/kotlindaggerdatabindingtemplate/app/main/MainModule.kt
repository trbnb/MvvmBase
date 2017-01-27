package de.trbnb.kotlindaggerdatabindingtemplate.app.main

import dagger.Module
import dagger.Provides
import de.trbnb.kotlindaggerdatabindingtemplate.base.dagger.ActivityScope

@Module
class MainModule() {

    @ActivityScope
    @Provides
    fun getViewModel() = MainViewModel()

}