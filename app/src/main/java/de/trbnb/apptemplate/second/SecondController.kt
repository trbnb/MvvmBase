package de.trbnb.apptemplate.second

import androidx.lifecycle.SavedStateHandle
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.app.appComponent
import de.trbnb.mvvmbase.conductor.MvvmController

class SecondController : MvvmController<SecondViewModel>(){
    override val layoutId: Int = R.layout.fragment_second
    override fun createViewModel(savedStateHandle: SavedStateHandle): SecondViewModel {
        return appComponent.secondViewModelFactory(savedStateHandle)
    }
}
