package de.trbnb.mvvmbase.rx

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable

/**
 * Interface that declares extension functions for RxKotlin classes.
 * To be used with [ViewModel].
 */
interface RxViewModel : ViewModel {
    /**
     * Converts an [Observable] of non-nullable type `T` into a bindable property of nullable type `T`.
     */
    fun <T : Any> Observable<T>.toBindable(
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ): ObservableBindableProperty<T> {
        return ObservableBindableProperty(this@RxViewModel, fieldId, this, onError, onComplete)
    }

    /**
     * Converts a [Flowable] of non-nullable type `T` into a bindable property of nullable type `T`.
     */
    fun <T : Any> Flowable<T>.toBindable(
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ): FlowableBindableProperty<T> {
        return FlowableBindableProperty(this@RxViewModel, fieldId, this, onError, onComplete)
    }

    /**
     * Converts a [Single] of non-nullable type `T` into a bindable property of nullable type `T`.
     */
    fun <T : Any> Single<T>.toBindable(
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ): SingleBindableProperty<T> {
        return SingleBindableProperty(this@RxViewModel, fieldId, this, onError)
    }

    /**
     * Converts a [Maybe] of non-nullable type `T` into a bindable property of nullable type `T`.
     */
    fun <T : Any> Maybe<T>.toBindable(
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ): MaybeBindableProperty<T> {
        return MaybeBindableProperty(this@RxViewModel, fieldId, this, onError, onComplete)
    }

    /**
     * Converts a [Completable] without type into a bindable property of the primitive boolean type.
     */
    fun Completable.toBindable(fieldId: Int? = null, onError: (Throwable) -> Unit = onErrorStub): CompletableBindableProperty {
        return CompletableBindableProperty(this@RxViewModel, fieldId, this, onError)
    }

    /**
     * Automatically disposes a [Disposable] with the ViewModels lifecycle.
     *
     * @see getLifecycle
     */
    fun Disposable.autoDispose() = autoDispose(lifecycle)
}

private val onErrorStub: (Throwable) -> Unit = { }
private val onCompleteStub = { }
