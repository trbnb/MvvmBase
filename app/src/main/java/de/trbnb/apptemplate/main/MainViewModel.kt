package de.trbnb.apptemplate.main

import android.annotation.SuppressLint
import android.databinding.Bindable
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.afterSet
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.commands.simpleCommand
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.rx.RxViewModel
import io.reactivex.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
class MainViewModel @Inject constructor() : BaseViewModel(), RxViewModel {

    @get:Bindable
    var isShowingDialog by bindableBoolean(false)

    @get:Bindable
    var showSnackbar: Boolean by bindableBoolean( false)
        .afterSet {
            showSnackbarCommand.onEnabledChanged()
        }

    @get:Bindable
    val title by Observable.create<String> {
        it.onNext("Foo")
        GlobalScope.launch {
            delay(5000)
            it.onNext("bar")
        }
    }.toBindable()

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
