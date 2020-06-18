package de.trbnb.apptemplate.second

import androidx.activity.ComponentActivity
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories.getActivityFactory
import de.trbnb.apptemplate.R
import de.trbnb.mvvmbase.conductor.MvvmController
import de.trbnb.mvvmbase.conductor.activityViewModels

class SecondController : MvvmController<SecondViewModel>() {
    override val layoutId: Int = R.layout.fragment_second
    override val viewModelDelegate = activityViewModels<SecondViewModel> {
        getActivityFactory(activity as? ComponentActivity ?: throw RuntimeException("Can't retrieve Activity")) ?: defaultViewModelProviderFactory
    }
}
