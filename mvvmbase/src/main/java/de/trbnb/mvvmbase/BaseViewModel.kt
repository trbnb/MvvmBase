package de.trbnb.mvvmbase

import android.databinding.BaseObservable

/**
 * Simple base implementation of the [ViewModel] interface based on [BaseObservable].
 *
 * The functions [onDestroy] and [onViewFinishing] are no-op so not every class that is extending
 * this one has to override them.
 */
abstract class BaseViewModel : BaseObservable(), ViewModel {

    override fun onDestroy() {

    }

    override fun onViewFinishing() {

    }

}