package de.trbnb.apptemplate.main

import android.content.Context
import android.databinding.Bindable
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.app.App
import de.trbnb.apptemplate.second.SecondActivity
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.afterSet
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.commands.simpleCommand
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import javax.inject.Inject

class MainViewModel : BaseViewModel() {

    @Inject
    lateinit var context: Context

    val text: String

    @get:Bindable
    var isShowingDialog by bindableBoolean(false)

    @get:Bindable
    var showSnackbar: Boolean by bindableBoolean( false)
            .afterSet {
                showSnackbarCommand.onEnabledChanged()
            }

    val showDialogCommand = simpleCommand {
        isShowingDialog = true
    }

    val showSnackbarCommand = ruleCommand(
            action = { showSnackbar = true },
            enabledRule = { !showSnackbar }
    )

    val showFragmentExampleCommand = simpleCommand {
        context.startActivity(context.intentFor<SecondActivity>().newTask())
    }

    val showMainActivityAgain = simpleCommand {
        context.startActivity(context.intentFor<MainActivity>().newTask())
    }

    init {
        App.appComponent.inject(this)

        text = context.getString(R.string.example_text)
    }

}
