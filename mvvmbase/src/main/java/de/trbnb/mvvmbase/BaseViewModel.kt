package de.trbnb.mvvmbase

import android.databinding.BaseObservable

/**
 * Simple base implementation of the [ViewModel] interface based on [BaseObservable].
 *
 * The function [onDestroy] is no-op so not every class that is extending this one has to override
 * it.
 */
abstract class BaseViewModel : BaseObservable(), ViewModel {

    override fun onDestroy() {

    }

}