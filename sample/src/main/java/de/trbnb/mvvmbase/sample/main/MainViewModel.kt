package de.trbnb.mvvmbase.sample.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.observableproperty.observable
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import io.reactivex.Observable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseStateSavingViewModel(savedStateHandle) {
    var textInput by observable("")
        .distinct()

    val title = Observable.create<String> { emitter ->
        emitter.onNext("foo")
        viewModelScope.launch {
            delay(5000)
            emitter.onNext("bar")
        }
    }

    val showToastCommand = ruleCommand(
        action = { eventChannel(MainEvent.ShowToast(textInput)) },
        enabledRule = { textInput.isNotEmpty() },
        dependencyProperties = listOf(::textInput)
    )
}
