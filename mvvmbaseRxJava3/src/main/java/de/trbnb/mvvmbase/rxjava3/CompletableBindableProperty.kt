package de.trbnb.mvvmbase.rxjava3

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.kotlin.plusAssign

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
