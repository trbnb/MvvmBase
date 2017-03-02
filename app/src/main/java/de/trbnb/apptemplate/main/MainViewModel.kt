package de.trbnb.apptemplate.main

import android.content.Context
import android.databinding.Bindable
import de.trbnb.apptemplate.BR
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.app.App
import de.trbnb.apptemplate.second.SecondActivity
import de.trbnb.databindingcommands.command.RuleCommand
import de.trbnb.databindingcommands.command.SimpleCommand
import de.trbnb.mvvmbase.BaseViewModel
import org.jetbrains.anko.startActivity
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
            showSnackbarCommand.onEnabledChanged()
        }

    val showDialogCommand = SimpleCommand { showDialog = true }
    val showSnackbarCommand = RuleCommand({ showSnackbar = true }, { !showSnackbar })

    val showFragmentExampleCommand = SimpleCommand {
        context.startActivity<SecondActivity>()
    }

    init {
        App.appComponent.inject(this)

        text = context.getString(R.string.example_text)
    }

}