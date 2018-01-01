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
import de.trbnb.mvvmbase.afterSet
import de.trbnb.mvvmbase.bindable
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class MainViewModel : BaseViewModel() {

    @Inject
    lateinit var context: Context

    val text: String

    @get:Bindable
    var showDialog by bindable(BR.showDialog, false)

    @get:Bindable
    var showSnackbar: Boolean by bindable(BR.showSnackbar, false)
            .afterSet {
                showSnackbarCommand.onEnabledChanged()
            }

    val showDialogCommand = SimpleCommand<Context, Unit> {
        showDialog = true
    }

    val showSnackbarCommand = RuleCommand<Context, Unit>(
            action = { showSnackbar = true },
            enabledRule = { !showSnackbar }
    )

    val showFragmentExampleCommand = SimpleCommand { context: Context ->
        context.startActivity<SecondActivity>()
    }

    init {
        App.appComponent.inject(this)

        text = context.getString(R.string.example_text)
    }

    override fun onUnbind() {
        showDialogCommand.clearEnabledListeners()
        showSnackbarCommand.clearEnabledListeners()
        showFragmentExampleCommand.clearEnabledListeners()
    }

}