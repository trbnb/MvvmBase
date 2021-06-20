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
class BindableUIntProperty private constructor(
    viewModel: ViewModel,
    private val fieldId: Int,
    defaultValue: UInt,
    distinct: Boolean,
    private val stateSavingKey: String?,
    afterSet: AfterSet<UInt>?,
    beforeSet: BeforeSet<UInt>?,
    validate: Validate<UInt>?
) : BindablePropertyBase<UInt>(distinct, afterSet, beforeSet, validate) {
    private var value: UInt = when {
        stateSavingKey != null && viewModel is SavedStateHandleOwner && stateSavingKey in viewModel.savedStateHandle -> {
            viewModel.savedStateHandle.get<Int>(stateSavingKey)?.toUInt() ?: defaultValue
        }
        else -> defaultValue
    }

    /**
     * @see [kotlin.properties.ReadWriteProperty.getValue]
     */
    operator fun getValue(thisRef: ViewModel, property: KProperty<*>): UInt = value

    /**
     * @see [kotlin.properties.ReadWriteProperty.setValue]
     */
    operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: UInt) {
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
            thisRef.savedStateHandle[stateSavingKey] = this.value.toInt()
        }
        afterSet?.invoke(oldValue, this.value)
    }

    /**
     * Property delegate provider for [BindableUIntProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see BindableUIntProperty
     */
    class Provider internal constructor(
        private val fieldId: Int? = null,
        private val defaultValue: UInt,
        private val stateSaveOption: StateSaveOption
    ) : BindablePropertyBase.Provider<ViewModel, UInt>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = BindableUIntProperty(
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
 * Creates a new [BindableUIntProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
fun ViewModel.bindableUInt(
    defaultValue: UInt = 0U,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = (this as? SavedStateHandleOwner)?.defaultStateSaveOption ?: StateSaveOption.None
) = BindableUIntProperty.Provider(fieldId, defaultValue, when (this) {
    is SavedStateHandleOwner -> stateSaveOption
    else -> StateSaveOption.None
})
