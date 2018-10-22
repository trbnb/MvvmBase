package de.trbnb.apptemplate.main

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.Bindable
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.second.SecondActivity
import de.trbnb.databindingcommands.command.RuleCommand
import de.trbnb.databindingcommands.command.SimpleCommand
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.afterSet
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.events.Event
import org.jetbrains.anko.startActivity
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
class MainViewModel @Inject constructor(private val context: Context) : BaseViewModel() {

    val text: String = context.getString(R.string.example_text)

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

    val showToastCommand = SimpleCommand {
        eventChannel(MainEvent.ShowToast)
    }

    val showFragmentExampleCommand = SimpleCommand {
        context.startActivity<SecondActivity>()
    }

    val showMainActivityAgain = SimpleCommand {
        context.startActivity<MainActivity>()
    }

    override fun onUnbind() {
        showDialogCommand.clearEnabledListeners()
        showSnackbarCommand.clearEnabledListeners()
        showFragmentExampleCommand.clearEnabledListeners()
    }

}

sealed class MainEvent : Event {
    object ShowToast : MainEvent()
}
