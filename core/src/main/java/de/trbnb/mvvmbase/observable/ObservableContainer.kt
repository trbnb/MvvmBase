package de.trbnb.mvvmbase.observable

import de.trbnb.mvvmbase.OnPropertyChangedCallback
import kotlin.reflect.KProperty

/**
 * Interface that describes basic functionality for classes that contain observable properties.
 */
public interface ObservableContainer {
    /**
     * Adds a callback that will be notified when a propertys value has changed.
     */
    public fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback)

    /**
     * Removes a callback.
     *
     * @see addOnPropertyChangedCallback
     */
    public fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback)

    /**
     * Notifies all callbacks that a propertys value may have changed.
     *
     * @param propertyName The name of the property.
     */
    public fun notifyPropertyChanged(propertyName: String)

    /**
     * Notifies all callbacks that a propertys value may have changed.
     *
     * @param property A reference of the property.
     */
    public fun notifyPropertyChanged(property: KProperty<*>): Unit = notifyPropertyChanged(property.name)
}
