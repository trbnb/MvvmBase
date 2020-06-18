package de.trbnb.apptemplate.second

import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import de.trbnb.apptemplate.R
import de.trbnb.mvvmbase.MvvmFragment

@AndroidEntryPoint
class SecondFragment : MvvmFragment<SecondViewModel>() {
    override val layoutId: Int = R.layout.fragment_second

    override val viewModelDelegate = activityViewModels<SecondViewModel>()
}
