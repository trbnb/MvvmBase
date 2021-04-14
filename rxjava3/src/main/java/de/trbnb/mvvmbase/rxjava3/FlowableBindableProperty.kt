package de.trbnb.mvvmbase.rxjava3

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.bindableproperty.AfterSet
import de.trbnb.mvvmbase.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.bindableproperty.Validate
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.plusAssign
import kotlin.reflect.KProperty

/**
 * Read-only bindable property delegate that has last emitted value from a [Flowable] or `defaultValue` if no value has been emitted.
 */
class FlowableBindableProperty<T> private constructor(
    viewModel: ViewModel,
    defaultValue: T,
    propertyName: String,
    flowable: Flowable<out T>,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit,
    distinct: Boolean,
    afterSet: AfterSet<T>?,
    beforeSet: BeforeSet<T>?,
    validate: Validate<T>?
) : RxBindablePropertyBase<T>(viewModel, defaultValue, propertyName, distinct, afterSet, beforeSet, validate) {
    init {
        viewModel.compositeDisposable += flowable.subscribe({ value = it }, onError, onComplete)
    }

    /**
     * Property delegate provider for [FlowableBindableProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see FlowableBindableProperty
     */
    class Provider<T> internal constructor(
        private val defaultValue: T,
        private val flowable: Flowable<out T>,
        private val onError: (Throwable) -> Unit,
        private val onComplete: () -> Unit
    ) : BindablePropertyBase.Provider<T>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = FlowableBindableProperty(
            viewModel = thisRef,
            propertyName = property.name,
            defaultValue = defaultValue,
            flowable = flowable,
            onError = onError,
            onComplete = onComplete,
            distinct = distinct,
            afterSet = afterSet,
            beforeSet = beforeSet,
            validate = validate
        )
    }
}
