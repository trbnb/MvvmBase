package de.trbnb.base.mvvm

import android.databinding.BaseObservable

abstract class BaseViewModel<V : MvvmView> : BaseObservable(), ViewModel<V> {

    override var view: V? = null

    override fun onDestroy() {

    }

}