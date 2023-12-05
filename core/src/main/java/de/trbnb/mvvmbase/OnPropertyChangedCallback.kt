package de.trbnb.mvvmbase

import de.trbnb.mvvmbase.observable.ObservableContainer

/**
 * Defines a simple callback for [ObservableContainer].
 */
public fun interface OnPropertyChangedCallback {
    /**
     * Called by an ObservableContainer whenever an observable property changes.
     *
     * @param sender The ObservableContainer that contains the property.
     * @param propertyName The name of the changed property.
     */
    public fun onPropertyChanged(sender: ObservableContainer, propertyName: String)
}
