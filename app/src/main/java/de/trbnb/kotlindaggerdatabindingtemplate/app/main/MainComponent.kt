package de.trbnb.kotlindaggerdatabindingtemplate.app.main

import dagger.Subcomponent
import de.trbnb.kotlindaggerdatabindingtemplate.base.dagger.ActivityScope

@ActivityScope
@Subcomponent(modules = arrayOf(
        MainModule::class
))
interface MainComponent{
    fun inject(mainActivity: MainActivity)
}