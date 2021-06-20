package de.trbnb.mvvmbase.databinding.bindableproperty

import androidx.databinding.BaseObservable
import de.trbnb.mvvmbase.bindableproperty.AfterSet
import de.trbnb.mvvmbase.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.bindableproperty.StateSaveOption
import de.trbnb.mvvmbase.bindableproperty.Validate
import de.trbnb.mvvmbase.bindableproperty.resolveKey
import de.trbnb.mvvmbase.databinding.ViewModel
import de.trbnb.mvvmbase.databinding.utils.resolveFieldId
import de.trbnb.mvvmbase.savedstate.SavedStateHandleOwner
import de.trbnb.mvvmbase.utils.savingStateInBindableSupports
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Delegate property that invokes [BaseObservable.notifyPropertyChanged] and saves state
 * via [SavedStateHandleOwner.savedStateHandle].
 *
 * @param T Type of the stored value.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param defaultValue Value that will be used at start if value can not be restored from [SavedStateHandleOwner.savedStateHandle].
 * @param distinct See [BindablePropertyBase.distinct].
 * @param stateSavingKey Specifies with which key the value will be state-saved. No state-saving if `null`.
 * @param afterSet [BindablePropertyBase.afterSet]
 * @param validate [BindablePropertyBase.validate]
 * @param beforeSet [BindablePropertyBase.beforeSet]
 */
class BindableProperty<T> internal constructor(
    viewModel: ViewModel,
    private val fieldId: Int,
    defaultValue: T,
    distinct: Boolean,
    private val stateSavingKey: String?,
    afterSet: AfterSet<T>?,
    beforeSet: BeforeSet<T>?,
    validate: Validate<T>?
) : BindablePropertyBase<T>(distinct, afterSet, beforeSet, validate), ReadWriteProperty<ViewModel, T> {
    @Suppress("UNCHECKED_CAST")
    private var value: T = when {
        stateSavingKey != null && viewModel is SavedStateHandleOwner && stateSavingKey in viewModel.savedStateHandle -> {
            viewModel.savedStateHandle.get<T>(stateSavingKey) as T
        }
        else -> defaultValue
    }

    override operator fun getValue(thisRef: ViewModel, property: KProperty<*>): T = value

    override operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: T) {
        if (distinct && this.value === value) {
            return
        }

        val oldValue = this.value
        beforeSet?.invoke(oldValue, value)
        this.value = when (val validate = validate) {
            null -> value
            else -> validate(oldValue, value)
        }

        thisRef.notifyPropertyChanged(fieldId)
        if (thisRef is SavedStateHandleOwner && stateSavingKey != null) {
            thisRef.savedStateHandle[stateSavingKey] = this.value
        }
        afterSet?.invoke(oldValue, this.value)
    }

    /**
     * Property delegate provider for [BindableProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see BindableProperty
     */
    class Provider<T>(
        private val fieldId: Int? = null,
        private val defaultValue: T,
        private val stateSaveOption: StateSaveOption
    ) : BindablePropertyBase.Provider<ViewModel, T>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>): BindableProperty<T> = BindableProperty(
            viewModel = thisRef,
            fieldId = fieldId ?: property.resolveFieldId(),
            defaultValue = defaultValue,
            stateSavingKey = stateSaveOption.resolveKey(property),
            distinct = distinct,
            afterSet = afterSet,
            beforeSet = beforeSet,
            validate = validate
        )
    }
}

/**
 * Creates a new BindableProperty instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
inline fun <reified T> ViewModel.bindable(
    defaultValue: T,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption? = null
): BindableProperty.Provider<T> = BindableProperty.Provider(fieldId, defaultValue, when (this) {
    is SavedStateHandleOwner -> when (stateSaveOption) {
        null -> when (savingStateInBindableSupports<T>()) {
            true -> defaultStateSaveOption
            false -> StateSaveOption.None
        }
        else -> stateSaveOption
    }
    else -> StateSaveOption.None
})

/**
 * Creates a new BindableProperty instance with `null` as default value.
 *
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
inline fun <reified T> ViewModel.bindable(
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption? = null
): BindableProperty.Provider<T?> = bindable(null, fieldId, stateSaveOption)