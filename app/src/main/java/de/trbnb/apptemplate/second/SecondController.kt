package de.trbnb.apptemplate.second

import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.resource.ResourceProviderImpl
import de.trbnb.mvvmbase.conductor.MvvmController
import de.trbnb.mvvmbase.conductor.viewModelProviderFactory

class SecondController : MvvmController<SecondViewModel>(layoutId = R.layout.fragment_second) {
    override fun getDefaultViewModelProviderFactory() = viewModelProviderFactory { handle ->
        SecondViewModel(handle, ResourceProviderImpl(activity ?: throw IllegalStateException()))
    }
}
