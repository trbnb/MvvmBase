package de.trbnb.mvvmbase.rx

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.Flowable
import io.reactivex.rxkotlin.subscribeBy

class FlowableBindableProperty<T : Any> internal constructor(
    viewModel: ViewModel,
    fieldId: Int?,
    flowable: Flowable<T>,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit
) : RxBindablePropertyBase<T>(viewModel, fieldId) {
    init {
        flowable.subscribeBy(onError = onError, onComplete = onComplete, onNext = { newValue ->
            value = newValue
        }).autoDispose(viewModel.lifecycle)
    }
}
