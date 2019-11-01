package de.trbnb.mvvmbase

import androidx.databinding.Observable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.SavedStateHandle
import de.trbnb.mvvmbase.events.EventChannel
import kotlin.reflect.KProperty

/**
 * Base interface that defines basic functionality for all view models.
 *
 * View models are bound to either an [MvvmBindingActivity] or an [MvvmBindingFragment] and saved
 * throughout the lifecycle of these by the Architecture Components.
 *
 * It extends the [Observable] interface provided by the Android data binding library. This means
 * that implementations have to handle [androidx.databinding.Observable.OnPropertyChangedCallback]s.
 * This is done the easiest way by extending [androidx.databinding.BaseObservable].
 */
interface ViewModel : Observable, LifecycleOwner {
    /**
     * Object that can be used to send one-time or not-state information to the UI.
     */
    val eventChannel: EventChannel

    val savedStateHandle: SavedStateHandle?

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    fun notifyChange()

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int)

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @see notifyPropertyChanged
     *
     * @param property The property whose BR field ID will be detected via reflection.
     */
    fun notifyPropertyChanged(property: KProperty<*>)

    /**
     * Registers a property changed callback.
     */
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback)

    /**
     * Unregisters a property changed callback.
     */
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback)

    /**
     * Is called when this ViewModel is bound to a View.
     */
    fun onBind()

    /**
     * Is called this ViewModel is not bound to a View anymore.
     */
    fun onUnbind()

    /**
     * Is called when this instance is about to be removed from memory.
     * This means that this object is no longer bound to a view and will never be. It is about to
     * be garbage collected.
     * Implementations should use this method to deregister from callbacks, etc.
     */
    fun onDestroy()

    fun setSavedStateHandle(savedStateHandle: SavedStateHandle)

    fun onRestore(action: (savedStateHandle: SavedStateHandle) -> Unit)

    /**
     * Destroys all ViewModels in that list when the containing ViewModel is destroyed.
     */
    fun <VM : ViewModel> List<VM>.autoDestroy() {
        lifecycle.addObserver(object : LifecycleObserver {
            @Suppress("unused")
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                forEach { it.onDestroy() }
            }
        })
    }
}
