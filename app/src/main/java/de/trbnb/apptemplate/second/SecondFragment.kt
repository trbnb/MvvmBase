package de.trbnb.apptemplate.second

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.resource.ResourceProviderImpl
import de.trbnb.mvvmbase.MvvmFragment

class SecondFragment : MvvmFragment<SecondViewModel>(R.layout.fragment_second) {
    override val viewModelDelegate = activityViewModels<SecondViewModel> { defaultViewModelProviderFactory }

    override fun getDefaultViewModelProviderFactory() = object : AbstractSavedStateViewModelFactory(this, arguments) {
        override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            return SecondViewModel(handle, ResourceProviderImpl(context ?: throw IllegalStateException())) as T
        }
    }
}
