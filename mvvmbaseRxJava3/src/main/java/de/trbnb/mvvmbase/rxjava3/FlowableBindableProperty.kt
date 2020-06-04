package de.trbnb.mvvmbase.rxjava3

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.plusAssign

/**
 * Read-only bindable property delegate that has last emitted value from a [Flowable] or `defaultValue` if no value has been emitted.
 */
class FlowableBindableProperty<T> internal constructor(
    viewModel: ViewModel,
    defaultValue: T,
    fieldId: Int?,
    flowable: Flowable<out T>,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit
) : RxBindablePropertyBase<T>(viewModel, defaultValue, fieldId) {
    init {
        viewModel.compositeDisposable += flowable.subscribe({ value = it }, onError, onComplete)
    }
}
