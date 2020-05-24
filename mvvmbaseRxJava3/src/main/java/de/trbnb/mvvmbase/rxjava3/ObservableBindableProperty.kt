package de.trbnb.mvvmbase.rxjava3

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.bindableproperty.AfterSet
import de.trbnb.mvvmbase.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.bindableproperty.Validate
import de.trbnb.mvvmbase.utils.resolveFieldId
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.plusAssign
import kotlin.reflect.KProperty

class ObservableBindableProperty<T> private constructor(
    viewModel: ViewModel,
    defaultValue: T,
    fieldId: Int,
    observable: Observable<out T>,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit,
    distinct: Boolean,
    afterSet: AfterSet<T>?,
    beforeSet: BeforeSet<T>?,
    validate: Validate<T>?
) : RxBindablePropertyBase<T>(viewModel, defaultValue, fieldId, distinct, afterSet, beforeSet, validate) {
    init {
        viewModel.compositeDisposable += observable.subscribe({ value = it }, onError, onComplete)
    }

    /**
     * Property delegate provider for [ObservableBindableProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see ObservableBindableProperty
     */
    class Provider<T>(
        private val fieldId: Int? = null,
        private val defaultValue: T,
        private val observable: Observable<out T>,
        private val onError: (Throwable) -> Unit,
        private val onComplete: () -> Unit
    ): BindablePropertyBase.Provider<T>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = ObservableBindableProperty(
            viewModel = thisRef,
            fieldId = fieldId ?: property.resolveFieldId(),
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
