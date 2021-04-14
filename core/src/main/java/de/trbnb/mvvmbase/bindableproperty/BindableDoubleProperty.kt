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
class BindableDoubleProperty private constructor(
    viewModel: ViewModel,
    defaultValue: Double,
    distinct: Boolean,
    private val stateSavingKey: String?,
    afterSet: AfterSet<Double>?,
    beforeSet: BeforeSet<Double>?,
    validate: Validate<Double>?
) : BindablePropertyBase<Double>(distinct, afterSet, beforeSet, validate) {
    private var value: Double = when {
        stateSavingKey != null && viewModel is StateSavingViewModel && stateSavingKey in viewModel.savedStateHandle -> {
            viewModel.savedStateHandle[stateSavingKey] ?: defaultValue
        }
        else -> defaultValue
    }

    /**
     * @see [kotlin.properties.ReadWriteProperty.getValue]
     */
    operator fun getValue(thisRef: ViewModel, property: KProperty<*>): Double = value

    /**
     * @see [kotlin.properties.ReadWriteProperty.setValue]
     */
    operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: Double) {
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
     * Property delegate provider for [BindableDoubleProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see BindableDoubleProperty
     */
    class Provider internal constructor(
        private val defaultValue: Double,
        private val stateSaveOption: StateSaveOption
    ) : BindablePropertyBase.Provider<Double>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = BindableDoubleProperty(
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
 * Creates a new [BindableDoubleProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
fun ViewModel.bindableDouble(
    defaultValue: Double = 0.0,
    stateSaveOption: StateSaveOption = (this as? StateSavingViewModel)?.defaultStateSaveOption ?: StateSaveOption.None
) = BindableDoubleProperty.Provider(defaultValue, when (this) {
    is StateSavingViewModel -> stateSaveOption
    else -> StateSaveOption.None
})
