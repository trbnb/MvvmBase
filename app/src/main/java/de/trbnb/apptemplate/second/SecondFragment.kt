package de.trbnb.apptemplate.second

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateHandle
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.app.appComponent
import de.trbnb.mvvmbase.MvvmFragment

class SecondFragment : MvvmFragment<SecondViewModel>() {
    override val layoutId: Int = R.layout.fragment_second

    override val viewModelDelegate = activityViewModels<SecondViewModel> { viewModelFactory }

    override fun createViewModel(savedStateHandle: SavedStateHandle): SecondViewModel {
        return appComponent.secondViewModelFactory(savedStateHandle)
    }
}
