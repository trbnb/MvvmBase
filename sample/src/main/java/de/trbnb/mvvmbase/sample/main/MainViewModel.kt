package de.trbnb.mvvmbase.sample.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.commands.simpleCommand
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import io.reactivex.Observable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseStateSavingViewModel(savedStateHandle) {
    var textInput by bindable("")
        .distinct()

    val title = Observable.create<String> { emitter ->
        emitter.onNext("foo")
        viewModelScope.launch {
            delay(5000)
            emitter.onNext("bar")
        }
    }

    val showToastCommand = simpleCommand {
        eventChannel(MainEvent.ShowToast)
    }
}
