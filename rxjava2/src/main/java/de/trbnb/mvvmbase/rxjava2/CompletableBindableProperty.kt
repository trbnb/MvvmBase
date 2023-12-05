package de.trbnb.mvvmbase.rxjava2

import de.trbnb.mvvmbase.databinding.ViewModel
import de.trbnb.mvvmbase.databinding.bindableproperty.AfterSet
import de.trbnb.mvvmbase.databinding.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.databinding.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.databinding.bindableproperty.Validate
import de.trbnb.mvvmbase.databinding.utils.resolveFieldId
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import kotlin.reflect.KProperty

/**
 * BindableProperty implementation for [Completable]s.
 */
public class CompletableBindableProperty private constructor(
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
    public class Provider internal constructor(
        private val completable: Completable,
        private val onError: (Throwable) -> Unit
    ) : BindablePropertyBase.Provider<ViewModel, Boolean>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>): CompletableBindableProperty = CompletableBindableProperty(
            viewModel = thisRef,
            fieldId = property.resolveFieldId(),
            completable = completable,
            onError = onError,
            distinct = distinct,
            afterSet = afterSet,
            beforeSet = beforeSet,
            validate = validate
        )
    }
}
