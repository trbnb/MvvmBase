package de.trbnb.mvvmbase.bindableproperty

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.savedstate.StateSavingViewModel
import kotlin.reflect.KProperty

/**
 * Delegate property that invokes [BaseObservable.notifyPropertyChanged] and saves state
 * via [StateSavingViewModel.savedStateHandle].
 *
 * @param defaultValue Value that will be used at start.
 * @param distinct See [BindablePropertyBase.distinct].
 * @param stateSavingKey Specifies with which key the value will be state-saved. No state-saving if `null`.
 * @param afterSet [BindablePropertyBase.afterSet]
 * @param validate [BindablePropertyBase.validate]
 * @param beforeSet [BindablePropertyBase.beforeSet]
 */
class BindableLongProperty private constructor(
    viewModel: ViewModel,
    defaultValue: Long,
    distinct: Boolean,
    private val stateSavingKey: String?,
    afterSet: AfterSet<Long>?,
    beforeSet: BeforeSet<Long>?,
    validate: Validate<Long>?
) : BindablePropertyBase<Long>(distinct, afterSet, beforeSet, validate) {
    private var value: Long = when {
        stateSavingKey != null && viewModel is StateSavingViewModel && stateSavingKey in viewModel.savedStateHandle -> {
            viewModel.savedStateHandle[stateSavingKey] ?: defaultValue
        }
        else -> defaultValue
    }

    /**
     * @see [kotlin.properties.ReadWriteProperty.getValue]
     */
    operator fun getValue(thisRef: ViewModel, property: KProperty<*>): Long = value

    /**
     * @see [kotlin.properties.ReadWriteProperty.setValue]
     */
    operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: Long) {
        if (distinct && this.value == value) {
            return
        }

        val oldValue = this.value
        beforeSet?.invoke(oldValue, value)
        this.value = when (val validate = validate) {
            null -> value
            else -> validate(oldValue, value)
        }

        thisRef.notifyPropertyChanged(property.name)
        if (thisRef is StateSavingViewModel && stateSavingKey != null) {
            thisRef.savedStateHandle[stateSavingKey] = this.value
        }
        afterSet?.invoke(oldValue, this.value)
    }

    /**
     * Property delegate provider for [BindableLongProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see BindableLongProperty
     */
    class Provider internal constructor(
        private val defaultValue: Long,
        private val stateSaveOption: StateSaveOption
    ) : BindablePropertyBase.Provider<Long>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = BindableLongProperty(
            viewModel = thisRef,
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
 * Creates a new [BindableLongProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
fun ViewModel.bindableLong(
    defaultValue: Long = 0,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
) = BindableLongProperty.Provider(defaultValue, when (this) {
    is StateSavingViewModel -> stateSaveOption
    else -> StateSaveOption.None
})
