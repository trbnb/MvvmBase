package de.trbnb.apptemplate.main

import androidx.databinding.Bindable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import de.trbnb.apptemplate.resource.ResourceProvider
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.commands.simpleCommand
import de.trbnb.mvvmbase.rxjava2.RxViewModel
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import io.reactivex.Observable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    resourceProvider: ResourceProvider
) : BaseStateSavingViewModel(savedStateHandle), RxViewModel {
    @get:Bindable
    var isShowingDialog by bindableBoolean(false)

    @get:Bindable
    var showSnackbar: Boolean by bindableBoolean(false)

    @get:Bindable
    val title: String by Observable.create<String> {
        viewModelScope.launch {
            delay(5000)
            it.onNext("bar")
        }
    }.toBindable(defaultValue = "foo")

    val showDialogCommand = ruleCommand(
        enabledRule = { !isShowingDialog },
        action = { isShowingDialog = true },
        dependentFields = listOf(::isShowingDialog)
    )

    val showSnackbarCommand = ruleCommand(
        action = { showSnackbar = true },
        enabledRule = { !showSnackbar },
        dependentFields = listOf(::showSnackbar)
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
