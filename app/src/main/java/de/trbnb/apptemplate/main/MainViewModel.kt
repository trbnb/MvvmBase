package de.trbnb.apptemplate.main

import android.content.Context
import android.databinding.Bindable
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
    var isShowingDialog by bindable(false)

    @get:Bindable
    var showSnackbar: Boolean by bindable( false)
            .afterSet {
                showSnackbarCommand.onEnabledChanged()
            }

    val showDialogCommand = SimpleCommand {
        isShowingDialog = true
    }

    val showSnackbarCommand = RuleCommand(
            action = { showSnackbar = true },
            enabledRule = { !showSnackbar }
    )

    val showFragmentExampleCommand = SimpleCommand {
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
