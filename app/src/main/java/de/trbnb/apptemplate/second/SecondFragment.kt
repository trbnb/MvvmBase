package de.trbnb.apptemplate.second

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.resource.ResourceProviderImpl
import de.trbnb.mvvmbase.MvvmFragment

class SecondFragment : MvvmFragment<SecondViewModel>() {
    override val layoutId: Int = R.layout.fragment_second

    override val viewModelDelegate = activityViewModels<SecondViewModel>()

    override fun getDefaultViewModelProviderFactory() = object : AbstractSavedStateViewModelFactory(this, defaultViewModelArgs) {
        override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            return SecondViewModel(handle, ResourceProviderImpl(context ?: throw IllegalStateException())) as T
        }
    }
}
