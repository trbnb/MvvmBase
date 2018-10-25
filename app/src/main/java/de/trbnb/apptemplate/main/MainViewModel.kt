package de.trbnb.apptemplate.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.Bindable
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.second.SecondActivity
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.afterSet
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.commands.simpleCommand
import de.trbnb.mvvmbase.events.Event
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
class MainViewModel @Inject constructor(private val context: Context) : BaseViewModel() {

    val text: String = context.getString(R.string.example_text)

    @get:Bindable
    var isShowingDialog by bindableBoolean(false)

    @get:Bindable
    var isShowingSnackbar: Boolean by bindableBoolean( false)
            .afterSet {
                showSnackbarCommand.onEnabledChanged()
            }

    val showDialogCommand = simpleCommand {
        isShowingDialog = true
    }

    val showSnackbarCommand = ruleCommand(
            action = { isShowingSnackbar = true },
            enabledRule = { !isShowingSnackbar }
    )

    val showToastCommand = simpleCommand {
        eventChannel(MainEvent.ShowToast)
    }

    val showFragmentExampleCommand = simpleCommand {
        context.startActivity(context.intentFor<SecondActivity>().newTask())
    }
    val showMainActivityAgain = simpleCommand {
        context.startActivity(context.intentFor<MainActivity>().newTask())
    }
}

sealed class MainEvent : Event {
    object ShowToast : MainEvent()
}
