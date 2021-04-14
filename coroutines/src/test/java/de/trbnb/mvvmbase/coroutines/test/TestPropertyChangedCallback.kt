package de.trbnb.mvvmbase.coroutines.test

import de.trbnb.mvvmbase.OnPropertyChangedCallback
import de.trbnb.mvvmbase.observable.ObservableContainer

class TestPropertyChangedCallback : OnPropertyChangedCallback {
    var changedPropertyIds: List<String> = emptyList()
    override fun onPropertyChanged(sender: ObservableContainer, propertyName: String) {
        changedPropertyIds = changedPropertyIds.toMutableList().apply { add(propertyName) }
    }
}
