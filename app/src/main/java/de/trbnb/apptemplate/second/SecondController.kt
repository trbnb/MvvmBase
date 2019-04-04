package de.trbnb.apptemplate.second

import de.trbnb.apptemplate.R
import de.trbnb.mvvmbase.conductor.MvvmController
import javax.inject.Provider

class SecondController : MvvmController<SecondViewModel>(){
    override val layoutId: Int = R.layout.fragment_second
    override val viewModelProvider: Provider<SecondViewModel> = Provider(::SecondViewModel)
}
