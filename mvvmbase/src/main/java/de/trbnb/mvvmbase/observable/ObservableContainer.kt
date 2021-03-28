package de.trbnb.mvvmbase.observable

import de.trbnb.mvvmbase.OnPropertyChangedCallback

interface ObservableContainer {
    fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback)
    fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback)

    fun notifyPropertyChanged(propertyName: String)
}
