package de.trbnb.mvvmbase.rx

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

class ObservableBindableProperty<T : Any> internal constructor(
    viewModel: ViewModel,
    fieldId: Int?,
    observable: Observable<T>,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit
) : RxBindablePropertyBase<T>(viewModel, fieldId) {
    init {
        observable.subscribeBy(onError = onError, onComplete = onComplete, onNext = { newValue ->
            value = newValue
        }).autoDispose(viewModel.lifecycle)
    }
}
