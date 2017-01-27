package de.trbnb.apptemplate.main

import dagger.Subcomponent
import de.trbnb.base.dagger.ActivityScope

@ActivityScope
@Subcomponent(modules = arrayOf(
        MainModule::class
))
interface MainComponent{
    fun inject(mainActivity: MainActivity)
}