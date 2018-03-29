package de.trbnb.mvvmbase

import android.databinding.Observable

class PropertyChangedMapListener  : Observable.OnPropertyChangedCallback() {
    private val fieldsMap = mutableMapOf<Int, Int>()

    val fieldChangedMap: Map<Int, Int>
        get() = fieldsMap

    override fun onPropertyChanged(sender: Observable, propertyId: Int) {
        fieldsMap[propertyId] = fieldsMap[propertyId]?.plus(1) ?: 1
    }
}
