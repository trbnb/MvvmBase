package de.trbnb.base.mvvm

interface ViewModel<V : MvvmView> {

    var view: V?

    fun onDestroy()

}