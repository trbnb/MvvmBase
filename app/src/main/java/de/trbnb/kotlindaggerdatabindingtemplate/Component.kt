package de.trbnb.kotlindaggerdatabindingtemplate

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(NetModule::class))
interface Component{
    fun inject(viewModel: MainViewModel)
}