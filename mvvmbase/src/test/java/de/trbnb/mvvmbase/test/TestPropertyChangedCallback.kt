package de.trbnb.mvvmbase.test

import androidx.databinding.Observable

class TestPropertyChangedCallback : Observable.OnPropertyChangedCallback() {
    var changedPropertyIds: List<Int> = emptyList()
        private set

    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        changedPropertyIds = changedPropertyIds.toMutableList().apply { add(propertyId) }
    }

    fun clear() {
        changedPropertyIds = emptyList()
    }
}
