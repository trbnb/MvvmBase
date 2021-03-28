package de.trbnb.apptemplate.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.commands.simpleCommand
import de.trbnb.mvvmbase.rxjava2.RxViewModel
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import io.reactivex.Observable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(
    savedStateHandle: SavedStateHandle
) : BaseStateSavingViewModel(savedStateHandle), RxViewModel {
    var textInput by bindable("")
        .distinct()

    val title: String by Observable.create<String> { emitter ->
        viewModelScope.launch {
            delay(5000)
            emitter.onNext("bar")
        }
    }.toBindable(defaultValue = "foo")

    val showDialogCommand = simpleCommand { eventChannel(MainEvent.ShowDialog) }

    val showSnackbarCommand = simpleCommand { eventChannel(MainEvent.ShowSnackbar("This is a sample Snackbar made with binding.")) }

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

    val showListCommand = simpleCommand {
        eventChannel(MainEvent.ShowListEvent)
    }
}
