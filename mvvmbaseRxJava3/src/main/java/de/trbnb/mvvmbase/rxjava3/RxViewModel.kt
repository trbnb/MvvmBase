package de.trbnb.mvvmbase.rxjava3

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable

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
        onError: (Throwable) -> Unit = onErrorStub
    ): SingleBindableProperty<T?> = SingleBindableProperty(this@RxViewModel, null, fieldId, this, onError)

    /**
     * Converts a [Single] of non-nullable type `T` into a bindable property.
     */
    fun <T> Single<T>.toBindable(
        defaultValue: T,
        fieldId: Int? = null,
        onError: (Throwable) -> Unit = onErrorStub
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
     * Automatically disposes a [Disposable] in the ViewModels [onDestroy].
     *
     * @see ViewModel.compositeDisposable
     */
    fun Disposable.autoDispose() {
        compositeDisposable.add(this)
    }
}

private val onErrorStub: (Throwable) -> Unit
    get() = {}

private val onCompleteStub: () -> Unit
    get() = {}
