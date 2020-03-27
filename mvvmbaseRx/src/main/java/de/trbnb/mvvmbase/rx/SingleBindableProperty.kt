package de.trbnb.mvvmbase.rx

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign

class SingleBindableProperty<T> internal constructor(
    viewModel: ViewModel,
    fieldId: Int?,
    single: Single<T>,
    onError: (Throwable) -> Unit
) : RxBindablePropertyBase<T?>(viewModel, null, fieldId) {
    init {
        viewModel.compositeDisposable += single.subscribe({ value = it }, onError)
    }
}
