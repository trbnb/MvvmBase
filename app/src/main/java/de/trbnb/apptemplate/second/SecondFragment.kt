package de.trbnb.apptemplate.second

import de.trbnb.apptemplate.R
import de.trbnb.mvvmbase.MvvmFragment
import javax.inject.Provider

class SecondFragment : MvvmFragment<SecondViewModel>(){

    override val loaderID: Int
        get() = 42

    override val layoutId: Int
        get() = R.layout.fragment_second

    override val viewModelProvider: Provider<SecondViewModel>
        get() = Provider(::SecondViewModel)

}