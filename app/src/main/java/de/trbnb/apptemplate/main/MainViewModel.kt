package de.trbnb.apptemplate.main

import android.content.Context
import android.databinding.Bindable
import de.trbnb.apptemplate.BR
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.app.App
import de.trbnb.databindingcommands.command.SimpleCommand
import de.trbnb.mvvmbase.BaseViewModel
import javax.inject.Inject

class MainViewModel : BaseViewModel(){

    @Inject
    lateinit var context: Context

    val text: String

    var showDialog: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.showDialog)
        }

    var showSnackbar: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.showSnackbar)
        }

    val showDialogCommand = SimpleCommand { showDialog = true }
    val showSnackbarCommand = SimpleCommand { showSnackbar = true }

    init {
        App.appComponent.inject(this)

        text = context.getString(R.string.example_text)
    }

}