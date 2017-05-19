package de.trbnb.mvvmbase

import android.databinding.Observable

/**
 * Base interface that defines basic functionality for all view models.
 *
 * View models are bound to either an [MvvmActivity] or an [MvvmFragment] and saved throughout the
 * lifecycle of these by the Loader mechanism.
 * @see [ViewModelLoader]
 *
 * It extends the [Observable] interface provided by the Android data binding library. This means
 * that implementations have to handle [android.databinding.Observable.OnPropertyChangedCallback]s.
 * This is done the easiest way by extending [android.databinding.BaseObservable].
 */
interface ViewModel : Observable {

    /**
     * Is called when the [ViewModelLoader] that is saving this instance is reset.
     * This means that this object is no longer bound to a view and will never be. It is about to
     * be garbage collected.
     * Implementations should use this method to deregister from callbacks, etc.
     */
    fun onDestroy()
}