package de.trbnb.mvvmbase.bindableproperty

import androidx.databinding.BaseObservable
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.savedstate.StateSavingViewModel
import de.trbnb.mvvmbase.utils.resolveFieldId
import kotlin.reflect.KProperty

/**
 * Delegate property that invokes [BaseObservable.notifyPropertyChanged] and saves state
 * via [StateSavingViewModel.savedStateHandle].
 *
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param defaultValue Value that will be used at start.
 * @param distinct See [BindablePropertyBase.distinct].
 * @param stateSavingKey Specifies with which key the value will be state-saved. No state-saving if `null`.
 * @param afterSet [BindablePropertyBase.afterSet]
 * @param validate [BindablePropertyBase.validate]
 * @param beforeSet [BindablePropertyBase.beforeSet]
 */
class BindableCharProperty private constructor(
    viewModel: ViewModel,
    private val fieldId: Int,
    defaultValue: Char,
    distinct: Boolean,
    private val stateSavingKey: String?,
    afterSet: AfterSet<Char>?,
    beforeSet: BeforeSet<Char>?,
    validate: Validate<Char>?
) : BindablePropertyBase<Char>(distinct, afterSet, beforeSet, validate) {
    private var value: Char = when {
        stateSavingKey != null && viewModel is StateSavingViewModel && stateSavingKey in viewModel.savedStateHandle -> {
            viewModel.savedStateHandle[stateSavingKey] ?: defaultValue
        }
        else -> defaultValue
    }

    /**
     * @see [kotlin.properties.ReadWriteProperty.getValue]
     */
    operator fun getValue(thisRef: ViewModel, property: KProperty<*>): Char = value

    /**
     * @see [kotlin.properties.ReadWriteProperty.setValue]
     */
    operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: Char) {
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
        if (thisRef is StateSavingViewModel && stateSavingKey != null) {
            thisRef.savedStateHandle[stateSavingKey] = this.value
        }
        afterSet?.invoke(oldValue, this.value)
    }

    /**
     * Property delegate provider for [BindableCharProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see BindableCharProperty
     */
    class Provider internal constructor(
        private val fieldId: Int? = null,
        private val defaultValue: Char,
        private val stateSaveOption: StateSaveOption
    ) : BindablePropertyBase.Provider<Char>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = BindableCharProperty(
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
 * Creates a new [BindableCharProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
fun ViewModel.bindableChar(
    defaultValue: Char,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = StateSaveOption.Automatic
) = BindableCharProperty.Provider(fieldId, defaultValue, when (this) {
    is StateSavingViewModel -> stateSaveOption
    else -> StateSaveOption.None
})
