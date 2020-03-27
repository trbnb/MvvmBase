package de.trbnb.mvvmbase.rx

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign

class MaybeBindableProperty<T> internal constructor(
    viewModel: ViewModel,
    defaultValue: T,
    fieldId: Int?,
    maybe: Maybe<T>,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit
) : RxBindablePropertyBase<T>(viewModel, defaultValue, fieldId) {
    init {
        viewModel.compositeDisposable += maybe.subscribe({ value = it }, onError, onComplete)
    }
}
