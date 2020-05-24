package de.trbnb.mvvmbase.rxjava3

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.bindableproperty.AfterSet
import de.trbnb.mvvmbase.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.bindableproperty.Validate
import de.trbnb.mvvmbase.utils.resolveFieldId
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.kotlin.plusAssign
import kotlin.reflect.KProperty

/**
 * BindableProperty implementation for [Completable]s.
 */
class CompletableBindableProperty private constructor(
    viewModel: ViewModel,
    fieldId: Int,
    completable: Completable,
    onError: (Throwable) -> Unit,
    distinct: Boolean,
    afterSet: AfterSet<Boolean>?,
    beforeSet: BeforeSet<Boolean>?,
    validate: Validate<Boolean>?
) : RxBindablePropertyBase<Boolean>(viewModel, false, fieldId, distinct, afterSet, beforeSet, validate) {
    init {
        viewModel.compositeDisposable += completable.subscribe({ value = true }, onError)
    }

    /**
     * Property delegate provider for [CompletableBindableProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see CompletableBindableProperty
     */
    class Provider(
        private val fieldId: Int? = null,
        private val completable: Completable,
        private val onError: (Throwable) -> Unit
    ): BindablePropertyBase.Provider<Boolean>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = CompletableBindableProperty(
            viewModel = thisRef,
            fieldId = fieldId ?: property.resolveFieldId(),
            completable = completable,
            onError = onError,
            distinct = distinct,
            afterSet = afterSet,
            beforeSet = beforeSet,
            validate = validate
        )
    }
}
