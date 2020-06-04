package de.trbnb.mvvmbase.rxjava2

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign

/**
 * Read-only bindable property delegate that has last emitted value from a [Single] or `defaultValue` if no value has been emitted.
 */
class SingleBindableProperty<T> internal constructor(
    viewModel: ViewModel,
    defaultValue: T,
    fieldId: Int?,
    single: Single<out T>,
    onError: (Throwable) -> Unit
) : RxBindablePropertyBase<T?>(viewModel, defaultValue, fieldId) {
    init {
        viewModel.compositeDisposable += single.subscribe({ value = it }, onError)
    }
}
