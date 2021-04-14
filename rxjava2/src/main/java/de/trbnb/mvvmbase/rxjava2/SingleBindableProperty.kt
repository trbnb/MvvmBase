package de.trbnb.mvvmbase.rxjava2

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.bindableproperty.AfterSet
import de.trbnb.mvvmbase.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.bindableproperty.Validate
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import kotlin.reflect.KProperty

/**
 * Read-only bindable property delegate that has last emitted value from a [Single] or `defaultValue` if no value has been emitted.
 */
class SingleBindableProperty<T> private constructor(
    viewModel: ViewModel,
    defaultValue: T,
    propertyName: String,
    single: Single<out T>,
    onError: (Throwable) -> Unit,
    distinct: Boolean,
    afterSet: AfterSet<T>?,
    beforeSet: BeforeSet<T>?,
    validate: Validate<T>?
) : RxBindablePropertyBase<T>(viewModel, defaultValue, propertyName, distinct, afterSet, beforeSet, validate) {
    init {
        viewModel.compositeDisposable += single.subscribe({ value = it }, onError)
    }

    /**
     * Property delegate provider for [SingleBindableProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see SingleBindableProperty
     */
    class Provider<T> internal constructor(
        private val defaultValue: T,
        private val single: Single<out T>,
        private val onError: (Throwable) -> Unit
    ) : BindablePropertyBase.Provider<T>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = SingleBindableProperty(
            viewModel = thisRef,
            propertyName = property.name,
            defaultValue = defaultValue,
            single = single,
            onError = onError,
            distinct = distinct,
            afterSet = afterSet,
            beforeSet = beforeSet,
            validate = validate
        )
    }
}
