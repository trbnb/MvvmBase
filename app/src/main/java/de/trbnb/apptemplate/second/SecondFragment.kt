package de.trbnb.apptemplate.second

import androidx.fragment.app.activityViewModels
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.resource.ResourceProviderImpl
import de.trbnb.mvvmbase.MvvmFragment
import de.trbnb.mvvmbase.viewmodel.viewModelProviderFactory

class SecondFragment : MvvmFragment<SecondViewModel>(R.layout.fragment_second) {
    override val viewModelDelegate = activityViewModels<SecondViewModel> { defaultViewModelProviderFactory }

    override fun getDefaultViewModelProviderFactory() = viewModelProviderFactory { handle ->
        SecondViewModel(handle, ResourceProviderImpl(context ?: throw IllegalStateException()))
    }
}
