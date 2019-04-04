package de.trbnb.mvvmbase.rx

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.Maybe
import io.reactivex.rxkotlin.subscribeBy

class MaybeBindableProperty<T : Any> internal constructor(
    viewModel: ViewModel,
    fieldId: Int?,
    maybe: Maybe<T>,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit
) : RxBindablePropertyBase<T>(viewModel, fieldId) {
    init {
        maybe.subscribeBy(onError = onError, onComplete = onComplete, onSuccess = { newValue ->
            value = newValue
        }).autoDispose(viewModel.lifecycle)
    }
}
