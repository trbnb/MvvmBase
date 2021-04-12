package de.trbnb.apptemplate.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.commands.simpleCommand
import de.trbnb.mvvmbase.rxjava2.RxViewModel
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import io.reactivex.Observable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
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

    val showToastCommand = simpleCommand {
        eventChannel(MainEvent.ShowToast)
    }
}
