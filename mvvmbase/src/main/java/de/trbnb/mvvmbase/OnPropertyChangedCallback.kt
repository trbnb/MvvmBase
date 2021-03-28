package de.trbnb.mvvmbase

import de.trbnb.mvvmbase.observable.ObservableContainer

fun interface OnPropertyChangedCallback {
    /**
     * Called by an Observable whenever an observable property changes.
     * @param sender The Observable that is changing.
     * @param propertyName The BR identifier of the property that has changed. The getter
     * for this property should be annotated with [Bindable].
     */
    fun onPropertyChanged(sender: ObservableContainer, propertyName: String)
}