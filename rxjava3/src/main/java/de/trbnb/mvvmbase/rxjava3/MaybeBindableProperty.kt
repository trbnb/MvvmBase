package de.trbnb.mvvmbase.rxjava3

import de.trbnb.mvvmbase.databinding.ViewModel
import de.trbnb.mvvmbase.databinding.bindableproperty.AfterSet
import de.trbnb.mvvmbase.databinding.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.databinding.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.databinding.bindableproperty.Validate
import de.trbnb.mvvmbase.databinding.utils.resolveFieldId
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.kotlin.plusAssign
import kotlin.reflect.KProperty

/**
 * Read-only bindable property delegate that has last emitted value from a [Maybe] or `defaultValue` if no value has been emitted.
 */
class MaybeBindableProperty<T> private constructor(
    viewModel: ViewModel,
    defaultValue: T,
    fieldId: Int,
    maybe: Maybe<out T>,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit,
    distinct: Boolean,
    afterSet: AfterSet<T>?,
    beforeSet: BeforeSet<T>?,
    validate: Validate<T>?
) : RxBindablePropertyBase<T>(viewModel, defaultValue, fieldId, distinct, afterSet, beforeSet, validate) {
    init {
        viewModel.compositeDisposable += maybe.subscribe({ value = it }, onError, onComplete)
    }

    /**
     * Property delegate provider for [MaybeBindableProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see MaybeBindableProperty
     */
    class Provider<T> internal constructor(
        private val defaultValue: T,
        private val maybe: Maybe<out T>,
        private val onError: (Throwable) -> Unit,
        private val onComplete: () -> Unit
    ) : BindablePropertyBase.Provider<ViewModel, T>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = MaybeBindableProperty(
            viewModel = thisRef,
            fieldId = property.resolveFieldId(),
            defaultValue = defaultValue,
            maybe = maybe,
            onError = onError,
            onComplete = onComplete,
            distinct = distinct,
            afterSet = afterSet,
            beforeSet = beforeSet,
            validate = validate
        )
    }
}
