package de.trbnb.apptemplate.second

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.resource.ResourceProviderImpl
import de.trbnb.mvvmbase.conductor.MvvmController

class SecondController : MvvmController<SecondViewModel>() {
    override val layoutId: Int = R.layout.fragment_second

    override val defaultViewModelProviderFactory = object : AbstractSavedStateViewModelFactory(this, defaultViewModelArgs) {
        override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            return SecondViewModel(handle, ResourceProviderImpl(activity ?: throw IllegalStateException())) as T
        }
    }
}
