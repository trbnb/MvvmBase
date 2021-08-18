package de.trbnb.mvvmbase.observable

import de.trbnb.mvvmbase.OnPropertyChangedCallback
import kotlin.reflect.KProperty

/**
 * Interface that describes basic functionality for classes that contain observable properties.
 */
interface ObservableContainer {
    /**
     * Adds a callback that will be notified when a propertys value has changed.
     */
    fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback)

    /**
     * Removes a callback.
     *
     * @see addOnPropertyChangedCallback
     */
    fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback)

    /**
     * Notifies all callbacks that a propertys value may have changed.
     *
     * @param propertyName The name of the property.
     */
    fun notifyPropertyChanged(propertyName: String)

    /**
     * Notifies all callbacks that a propertys value may have changed.
     *
     * @param property A reference of the property.
     */
    fun notifyPropertyChanged(property: KProperty<*>) = notifyPropertyChanged(property.name)
}
