package de.trbnb.mvvmbase.rx

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign

/**
 * BindableProperty implementation for [Completable]s.
 */
class CompletableBindableProperty internal constructor(
    viewModel: ViewModel,
    fieldId: Int?,
    completable: Completable,
    onError: (Throwable) -> Unit
) : RxBindablePropertyBase<Boolean>(viewModel, false, fieldId) {
    init {
        viewModel.compositeDisposable += completable.subscribe({ value = true }, onError)
    }
}
