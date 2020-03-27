package de.trbnb.mvvmbase.rxjava2

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.Flowable
import io.reactivex.rxkotlin.plusAssign

class FlowableBindableProperty<T> internal constructor(
    viewModel: ViewModel,
    defaultValue: T,
    fieldId: Int?,
    flowable: Flowable<T>,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit
) : RxBindablePropertyBase<T>(viewModel, defaultValue, fieldId) {
    init {
        viewModel.compositeDisposable += flowable.subscribe({ value = it }, onError, onComplete)
    }
}
