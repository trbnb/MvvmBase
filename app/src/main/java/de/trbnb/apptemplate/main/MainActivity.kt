package de.trbnb.apptemplate.main

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import de.trbnb.apptemplate.R
import de.trbnb.base.mvvm.MvvmActivity
import javax.inject.Provider

class MainActivity : MvvmActivity<MainViewModel>() {

    override val viewModelProvider: Provider<MainViewModel>
        get() = Provider { MainViewModel() }

    override fun initBinding(): ViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

}
