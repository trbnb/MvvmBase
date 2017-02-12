package de.trbnb.apptemplate.main

import de.trbnb.apptemplate.R
import de.trbnb.mvvmbase.MvvmActivity
import javax.inject.Provider

class MainActivity : MvvmActivity<MainViewModel>() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override val viewModelProvider: Provider<MainViewModel>
        get() = Provider { MainViewModel() }

}
