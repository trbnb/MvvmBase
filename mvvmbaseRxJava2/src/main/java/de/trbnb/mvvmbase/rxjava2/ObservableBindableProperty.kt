package de.trbnb.mvvmbase.rxjava2

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.bindableproperty.AfterSet
import de.trbnb.mvvmbase.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.bindableproperty.Validate
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import kotlin.reflect.KProperty

/**
 * Read-only bindable property delegate that has last emitted value from a [Observable] or `defaultValue` if no value has been emitted.
 */
class ObservableBindableProperty<T> private constructor(
    viewModel: ViewModel,
    defaultValue: T,
    propertyName: String,
    observable: Observable<out T>,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit,
    distinct: Boolean,
    afterSet: AfterSet<T>?,
    beforeSet: BeforeSet<T>?,
    validate: Validate<T>?
) : RxBindablePropertyBase<T>(viewModel, defaultValue, propertyName, distinct, afterSet, beforeSet, validate) {
    init {
        viewModel.compositeDisposable += observable.subscribe({ value = it }, onError, onComplete)
    }

    /**
     * Property delegate provider for [ObservableBindableProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see ObservableBindableProperty
     */
    class Provider<T> internal constructor(
        private val defaultValue: T,
        private val observable: Observable<out T>,
        private val onError: (Throwable) -> Unit,
        private val onComplete: () -> Unit
    ) : BindablePropertyBase.Provider<T>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = ObservableBindableProperty(
            viewModel = thisRef,
            propertyName = property.name,
            defaultValue = defaultValue,
            observable = observable,
            onError = onError,
            onComplete = onComplete,
            distinct = distinct,
            afterSet = afterSet,
            beforeSet = beforeSet,
            validate = validate
        )
    }
}
