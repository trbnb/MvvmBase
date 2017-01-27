package de.trbnb.kotlindaggerdatabindingtemplate.base.mvvm

interface ViewModel<V : MvvmView> {

    var view: V?

    fun onDestroy()

}