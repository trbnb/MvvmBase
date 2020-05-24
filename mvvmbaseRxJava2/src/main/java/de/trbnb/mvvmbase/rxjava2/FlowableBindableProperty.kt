package de.trbnb.mvvmbase.rxjava2

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.bindableproperty.AfterSet
import de.trbnb.mvvmbase.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.bindableproperty.Validate
import de.trbnb.mvvmbase.utils.resolveFieldId
import io.reactivex.Flowable
import io.reactivex.rxkotlin.plusAssign
import kotlin.reflect.KProperty

class FlowableBindableProperty<T> private constructor(
    viewModel: ViewModel,
    defaultValue: T,
    fieldId: Int,
    flowable: Flowable<out T>,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit,
    distinct: Boolean,
    afterSet: AfterSet<T>?,
    beforeSet: BeforeSet<T>?,
    validate: Validate<T>?
) : RxBindablePropertyBase<T>(viewModel, defaultValue, fieldId, distinct, afterSet, beforeSet, validate) {
    init {
        viewModel.compositeDisposable += flowable.subscribe({ value = it }, onError, onComplete)
    }

    /**
     * Property delegate provider for [FlowableBindableProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see FlowableBindableProperty
     */
    class Provider<T>(
        private val fieldId: Int? = null,
        private val defaultValue: T,
        private val flowable: Flowable<out T>,
        private val onError: (Throwable) -> Unit,
        private val onComplete: () -> Unit
    ): BindablePropertyBase.Provider<T>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = FlowableBindableProperty(
            viewModel = thisRef,
            fieldId = fieldId ?: property.resolveFieldId(),
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
