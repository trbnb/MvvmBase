package de.trbnb.mvvmbase

import androidx.databinding.Observable
import androidx.lifecycle.LifecycleOwner

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
     * Is called when this ViewModel is bound to a View.
     */
    fun onBind()

    /**
     * Is called this ViewModel is not bound to a View anymore.
     */
    fun onUnbind()

    /**
     * Is called when this instance is about to be removed from memory..
     * This means that this object is no longer bound to a view and will never be. It is about to
     * be garbage collected.
     * Implementations should use this method to deregister from callbacks, etc.
     */
    fun onDestroy()
}
