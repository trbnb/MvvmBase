package de.trbnb.apptemplate.main

import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.afterSet
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.commands.simpleCommand
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.rxjava2.RxViewModel
import io.reactivex.Observable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor() : BaseViewModel(), RxViewModel {

    @get:Bindable
    var isShowingDialog by bindableBoolean(false)

    @get:Bindable
    var showSnackbar: Boolean by bindableBoolean(false)
        .afterSet {
            showSnackbarCommand.onEnabledChanged()
        }

    @get:Bindable
    val title: String by Observable.create<String> {
        viewModelScope.launch {
            delay(5000)
            it.onNext("bar")
        }
    }.toBindable(defaultValue = "foo")

    val showDialogCommand = simpleCommand {
        isShowingDialog = true
    }

    val showSnackbarCommand = ruleCommand(
        action = { showSnackbar = true },
        enabledRule = { !showSnackbar }
    )

    val showToastCommand = simpleCommand {
        eventChannel(MainEvent.ShowToast)
    }

    val showFragmentExampleCommand = simpleCommand {
        eventChannel(MainEvent.ShowSecondActivityEvent)
    }

    val showMainActivityAgain = simpleCommand {
        eventChannel(MainEvent.ShowMainActivityAgainEvent)
    }

    val showConductorEvent = simpleCommand {
        eventChannel(MainEvent.ShowConductorEvent)
    }
}

sealed class MainEvent : Event {
    object ShowToast : MainEvent()
    object ShowSecondActivityEvent : MainEvent()
    object ShowMainActivityAgainEvent : MainEvent()
    object ShowConductorEvent : MainEvent()
}
