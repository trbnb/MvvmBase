package de.trbnb.mvvmbase.rx

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy

class SingleBindableProperty<T : Any> internal constructor(
    viewModel: ViewModel,
    fieldId: Int?,
    single: Single<T>,
    onError: (Throwable) -> Unit
) : RxBindablePropertyBase<T>(viewModel, fieldId) {
    init {
        single.subscribeBy(onError = onError, onSuccess = { newValue ->
            value = newValue
        }).autoDispose(viewModel.lifecycle)
    }
}
