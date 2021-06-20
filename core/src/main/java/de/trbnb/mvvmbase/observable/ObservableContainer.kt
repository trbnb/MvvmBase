package de.trbnb.mvvmbase.observable

import de.trbnb.mvvmbase.OnPropertyChangedCallback
import kotlin.reflect.KProperty

interface ObservableContainer {
    fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback)
    fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback)

    fun notifyPropertyChanged(propertyName: String)
    fun notifyPropertyChanged(property: KProperty<*>) = notifyPropertyChanged(property.name)
}
