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
    fun <T> Observable<T?>.toBindable(
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ): ObservableBindableProperty<T?> = ObservableBindableProperty(this@RxViewModel, null, fieldId, this, onError, onComplete)

    /**
     * Converts an [Observable] of non-nullable type `T` into a bindable property.
     */
    fun <T> Observable<T>.toBindable(
        defaultValue: T,
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ): ObservableBindableProperty<T> = ObservableBindableProperty(this@RxViewModel, defaultValue, fieldId, this, onError, onComplete)

    /**
     * Converts a [Flowable] of non-nullable type `T` into a bindable property of nullable type `T`.
     */
    fun <T> Flowable<T?>.toBindable(
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ): FlowableBindableProperty<T?> = FlowableBindableProperty(this@RxViewModel, null, fieldId, this, onError, onComplete)

    /**
     * Converts a [Flowable] of non-nullable type `T` into a bindable property.
     */
    fun <T> Flowable<T>.toBindable(
        defaultValue: T,
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ): FlowableBindableProperty<T> = FlowableBindableProperty(this@RxViewModel, defaultValue, fieldId, this, onError, onComplete)

    /**
     * Converts a [Single] of non-nullable type `T` into a bindable property of nullable type `T`.
     */
    fun <T> Single<T?>.toBindable(
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ): SingleBindableProperty<T?> = SingleBindableProperty(this@RxViewModel, null, fieldId, this, onError)

    /**
     * Converts a [Single] of non-nullable type `T` into a bindable property.
     */
    fun <T> Single<T>.toBindable(
        defaultValue: T,
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ): SingleBindableProperty<T> = SingleBindableProperty(this@RxViewModel, defaultValue, fieldId, this, onError)

    /**
     * Converts a [Maybe] of non-nullable type `T` into a bindable property of nullable type `T`.
     */
    fun <T> Maybe<T?>.toBindable(
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ): MaybeBindableProperty<T?> = MaybeBindableProperty(this@RxViewModel, null, fieldId, this, onError, onComplete)

    /**
     * Converts a [Maybe] of non-nullable type `T` into a bindable property.
     */
    fun <T> Maybe<T>.toBindable(
        defaultValue: T,
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub
    ): MaybeBindableProperty<T> = MaybeBindableProperty(this@RxViewModel, defaultValue, fieldId, this, onError, onComplete)

    /**
     * Converts a [Completable] without type into a bindable property of the primitive boolean type.
     */
    fun Completable.toBindable(
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub
    ): CompletableBindableProperty = CompletableBindableProperty(this@RxViewModel, fieldId, this, onError)

    /**
     * Automatically disposes a [Disposable] with the ViewModels lifecycle.
     *
     * @see getLifecycle
     */
    fun Disposable.autoDispose() = autoDispose(lifecycle)
}

private val onErrorStub: (Throwable) -> Unit
    get() = {}

private val onCompleteStub: () -> Unit
    get() = {}
