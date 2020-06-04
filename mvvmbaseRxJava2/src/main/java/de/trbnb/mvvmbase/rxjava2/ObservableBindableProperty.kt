package de.trbnb.mvvmbase.rxjava2

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign

/**
 * Read-only bindable property delegate that has last emitted value from a [Observable] or `defaultValue` if no value has been emitted.
 */
class ObservableBindableProperty<T> internal constructor(
    viewModel: ViewModel,
    defaultValue: T,
    fieldId: Int?,
    observable: Observable<out T>,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit
) : RxBindablePropertyBase<T>(viewModel, defaultValue, fieldId) {
    init {
        viewModel.compositeDisposable += observable.subscribe({ value = it }, onError, onComplete)
    }
}
