package de.trbnb.apptemplate.main

import android.content.Context
import android.databinding.Bindable
import de.trbnb.apptemplate.BR
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.app.App
import de.trbnb.base.mvvm.BaseViewModel
import javax.inject.Inject

class MainViewModel : BaseViewModel<MainActivity>(){

    @Inject
    lateinit var context: Context

    var text: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.text)
        }

    init {
        App.appComponent.inject(this)

        text = context.getString(R.string.example_text)
    }

}