package de.trbnb.mvvmbase

import android.databinding.BaseObservable

/**
 * Simple base implementation of the [ViewModel] interface based on [BaseObservable].
 */
abstract class BaseViewModel : BaseObservable(), ViewModel {

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

}
