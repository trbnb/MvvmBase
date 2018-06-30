package de.trbnb.apptemplate.second

import de.trbnb.apptemplate.R
import de.trbnb.mvvmbase.MvvmFragment
import org.koin.android.architecture.ext.getViewModel

class SecondFragment : MvvmFragment<SecondViewModel>() {
    override val layoutId = R.layout.fragment_second
    override val viewModelProvider = { getViewModel<SecondViewModel>() }
}