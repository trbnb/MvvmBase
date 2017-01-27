package de.trbnb.kotlindaggerdatabindingtemplate.app.main

import android.content.Context
import android.databinding.Bindable
import de.trbnb.kotlindaggerdatabindingtemplate.BR
import de.trbnb.kotlindaggerdatabindingtemplate.R
import de.trbnb.kotlindaggerdatabindingtemplate.app.app.App
import de.trbnb.kotlindaggerdatabindingtemplate.base.mvvm.BaseViewModel
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