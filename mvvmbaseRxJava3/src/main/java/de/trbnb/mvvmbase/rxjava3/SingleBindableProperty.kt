package de.trbnb.mvvmbase.rxjava3

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.plusAssign

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
