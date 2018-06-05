package de.trbnb.mvvmbase

import android.databinding.Observable

/**
 * Base interface that defines basic functionality for all view models.
 *
 * View models are bound to either an [MvvmBindingActivity] or an [MvvmBindingFragment] and saved
 * throughout the lifecycle of these by the Architecture Components.
 *
 * It extends the [Observable] interface provided by the Android data binding library. This means
 * that implementations have to handle [android.databinding.Observable.OnPropertyChangedCallback]s.
 * This is done the easiest way by extending [android.databinding.BaseObservable].
 */
interface ViewModel : Observable {

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
