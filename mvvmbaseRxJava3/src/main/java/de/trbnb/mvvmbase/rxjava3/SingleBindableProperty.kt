package de.trbnb.mvvmbase.rxjava3

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.bindableproperty.AfterSet
import de.trbnb.mvvmbase.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.bindableproperty.Validate
import de.trbnb.mvvmbase.utils.resolveFieldId
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.plusAssign
import kotlin.reflect.KProperty

class SingleBindableProperty<T> private constructor(
    viewModel: ViewModel,
    defaultValue: T,
    fieldId: Int,
    single: Single<out T>,
    onError: (Throwable) -> Unit,
    distinct: Boolean,
    afterSet: AfterSet<T?>?,
    beforeSet: BeforeSet<T?>?,
    validate: Validate<T?>?
) : RxBindablePropertyBase<T?>(viewModel, defaultValue, fieldId, distinct, afterSet, beforeSet, validate) {
    init {
        viewModel.compositeDisposable += single.subscribe({ value = it }, onError)
    }

    /**
     * Property delegate provider for [SingleBindableProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see SingleBindableProperty
     */
    class Provider<T>(
        private val fieldId: Int? = null,
        private val defaultValue: T,
        private val single: Single<out T>,
        private val onError: (Throwable) -> Unit
    ): BindablePropertyBase.Provider<T?>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = SingleBindableProperty(
            viewModel = thisRef,
            fieldId = fieldId ?: property.resolveFieldId(),
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
