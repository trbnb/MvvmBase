package de.trbnb.apptemplate.main

import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import de.trbnb.mvvmbase.BR
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.commands.simpleCommand
import de.trbnb.mvvmbase.rxjava2.RxViewModel
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import io.reactivex.Observable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("UndocumentedPublicClass")
class MainViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle
) : BaseStateSavingViewModel(savedStateHandle), RxViewModel {
    @Suppress("UndocumentedPublicProperty")
    @get:Bindable
    var isShowingDialog by bindableBoolean(false)

    @Suppress("UndocumentedPublicProperty")
    @get:Bindable
    var showSnackbar: Boolean by bindableBoolean(false)

    @Suppress("UndocumentedPublicProperty", "MagicNumber")
    @get:Bindable
    val title: String by Observable.create<String> {
        viewModelScope.launch {
            delay(5000)
            it.onNext("bar")
        }
    }.toBindable(defaultValue = "foo")

    @Suppress("UndocumentedPublicProperty")
    val showDialogCommand = ruleCommand(
        enabledRule = { !isShowingDialog },
        action = { isShowingDialog = true },
        dependentFieldIds = intArrayOf(BR.showingDialog)
    )

    @Suppress("UndocumentedPublicProperty")
    val showSnackbarCommand = ruleCommand(
        action = { showSnackbar = true },
        enabledRule = { !showSnackbar },
        dependentFieldIds = intArrayOf(BR.showSnackbar)
    )

    @Suppress("UndocumentedPublicProperty")
    val showToastCommand = simpleCommand {
        eventChannel(MainEvent.ShowToast)
    }

    @Suppress("UndocumentedPublicProperty")
    val showFragmentExampleCommand = simpleCommand {
        eventChannel(MainEvent.ShowSecondActivityEvent)
    }

    @Suppress("UndocumentedPublicProperty")
    val showMainActivityAgain = simpleCommand {
        eventChannel(MainEvent.ShowMainActivityAgainEvent)
    }

    @Suppress("UndocumentedPublicProperty")
    val showConductorEvent = simpleCommand {
        eventChannel(MainEvent.ShowConductorEvent)
    }

    @Suppress("UndocumentedPublicClass")
    @AssistedInject.Factory
    interface Factory {
        @Suppress("UndocumentedPublicFunction")
        operator fun invoke(savedStateHandle: SavedStateHandle): MainViewModel
    }
}
