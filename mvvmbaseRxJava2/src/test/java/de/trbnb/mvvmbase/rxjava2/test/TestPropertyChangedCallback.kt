package de.trbnb.mvvmbase.rxjava2.test

import androidx.databinding.Observable

class TestPropertyChangedCallback : Observable.OnPropertyChangedCallback() {
    var changedPropertyIds: List<Int> = emptyList()
    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        changedPropertyIds = changedPropertyIds.toMutableList().apply { add(propertyId) }
    }
}
