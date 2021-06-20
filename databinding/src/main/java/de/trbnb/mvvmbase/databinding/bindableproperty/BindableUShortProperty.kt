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
import kotlin.reflect.KProperty

/**
 * Delegate property that invokes [BaseObservable.notifyPropertyChanged] and saves state
 * via [SavedStateHandleOwner.savedStateHandle].
 *
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param defaultValue Value that will be used at start.
 * @param distinct See [BindablePropertyBase.distinct].
 * @param stateSavingKey Specifies with which key the value will be state-saved. No state-saving if `null`.
 * @param afterSet [BindablePropertyBase.afterSet]
 * @param validate [BindablePropertyBase.validate]
 * @param beforeSet [BindablePropertyBase.beforeSet]
 */
class BindableUShortProperty private constructor(
    viewModel: ViewModel,
    private val fieldId: Int,
    defaultValue: UShort,
    distinct: Boolean,
    private val stateSavingKey: String?,
    afterSet: AfterSet<UShort>?,
    beforeSet: BeforeSet<UShort>?,
    validate: Validate<UShort>?
) : BindablePropertyBase<UShort>(distinct, afterSet, beforeSet, validate) {
    private var value: UShort = when {
        stateSavingKey != null && viewModel is SavedStateHandleOwner && stateSavingKey in viewModel.savedStateHandle -> {
            viewModel.savedStateHandle.get<Short>(stateSavingKey)?.toUShort() ?: defaultValue
        }
        else -> defaultValue
    }

    /**
     * @see [kotlin.properties.ReadWriteProperty.getValue]
     */
    operator fun getValue(thisRef: ViewModel, property: KProperty<*>): UShort = value

    /**
     * @see [kotlin.properties.ReadWriteProperty.setValue]
     */
    operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: UShort) {
        if (distinct && this.value == value) {
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
            thisRef.savedStateHandle[stateSavingKey] = this.value.toShort()
        }
        afterSet?.invoke(oldValue, this.value)
    }

    /**
     * Property delegate provider for [BindableUShortProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see BindableUShortProperty
     */
    class Provider internal constructor(
        private val fieldId: Int? = null,
        private val defaultValue: UShort,
        private val stateSaveOption: StateSaveOption
    ) : BindablePropertyBase.Provider<ViewModel, UShort>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = BindableUShortProperty(
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
 * Creates a new [BindableUShortProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
fun ViewModel.bindableUShort(
    defaultValue: UShort = 0U,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? SavedStateHandleOwner)?.defaultStateSaveOption ?: StateSaveOption.None
) = BindableUShortProperty.Provider(fieldId, defaultValue, when (this) {
    is SavedStateHandleOwner -> stateSaveOption
    else -> StateSaveOption.None
})
