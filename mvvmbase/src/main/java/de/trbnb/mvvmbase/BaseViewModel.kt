package de.trbnb.mvvmbase

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry
import android.arch.lifecycle.ViewModel as ArchitectureViewModel

/**
 * Simple base implementation of the [ViewModel] interface based on [BaseObservable].
 */
abstract class BaseViewModel : ArchitectureViewModel(), Observable, ViewModel {

    /**
     * Callback registry for [Observable].
     */
    @Transient
    private var callbacks = PropertyChangeRegistry()

    /**
     * Adds a property changed callback to [callbacks].
     */
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    /**
     * Removes a property changed callback to [callbacks].
     */
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    /**
     * Is called when the view model is bound to an activity/layout.
     */
    override fun onBind() { }

    /**
     * Is called when the view model is no longer bound to an activity/layout.
     */
    override fun onUnbind() { }

    /**
     * Is called when this instance is about to be destroyed.
     * Any references that could cause memory leaks should be cleared here.
     */
    override fun onDestroy() { }

    final override fun onCleared() {
        super.onCleared()

        onDestroy()
    }

}
