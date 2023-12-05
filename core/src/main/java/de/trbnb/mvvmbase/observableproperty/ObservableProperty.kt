package de.trbnb.mvvmbase.observableproperty

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.savedstate.StateSavingViewModel
import de.trbnb.mvvmbase.utils.savingStateInBindableSupports
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public typealias BeforeSet<T> = (old: T, new: T) -> Unit
public typealias Validate<T> = (old: T, new: T) -> T
public typealias AfterSet<T> = (old: T, new: T) -> Unit

/**
 * Delegate property that invokes [de.trbnb.mvvmbase.observable.ObservableContainer.notifyPropertyChanged]
 * and saves state via [StateSavingViewModel.savedStateHandle].
 *
 * @param T Type of the stored value.
 * @param defaultValue Value that will be used at start if value can not be restored from [StateSavingViewModel.savedStateHandle].
 * @param stateSavingKey Specifies with which key the value will be state-saved. No state-saving if `null`.
 *
 * @param distinct Gets or sets whether the setter should check if a new value is not equal to the old value.
 * If true and a value is about to be set that is equal to the old one the setter will do nothing.
 *
 * @param afterSet Gets or sets a function that will be invoked if a new value was set and
 * [de.trbnb.mvvmbase.observable.ObservableContainer.notifyPropertyChanged] was invoked.
 * The first parameter is the old value and the second parameter is the new value.
 *
 * @param validate Gets or sets a function that will validate a newly set value.
 * The first parameter is the old value and the second parameter is the new value.
 * The returned value will be the new stored value.
 * If this function is null validation will not happen and the new value will simply be set.
 *
 * @param beforeSet Gets or sets a function that will be invoked if a new value is about to be set.
 * The first parameter is the old value and the second parameter is the new value.
 * This function will not be invoked if [ObservableProperty.distinct] is true and the new value is equal to the old value.
 */
public class ObservableProperty<T> internal constructor(
    viewModel: ViewModel,
    defaultValue: T,
    private val distinct: Boolean,
    private val stateSavingKey: String?,
    private val afterSet: AfterSet<T>?,
    private val beforeSet: BeforeSet<T>?,
    private val validate: Validate<T>?
) : ReadWriteProperty<ViewModel, T> {
    @Suppress("UNCHECKED_CAST")
    private var value: T = when {
        stateSavingKey != null && viewModel is StateSavingViewModel && stateSavingKey in viewModel.savedStateHandle -> {
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

        thisRef.notifyPropertyChanged(property)
        if (thisRef is StateSavingViewModel && stateSavingKey != null) {
            thisRef.savedStateHandle[stateSavingKey] = this.value
        }
        afterSet?.invoke(oldValue, this.value)
    }

    /**
     * Property delegate provider for [ObservableProperty].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see ObservableProperty
     */
    public class Provider<T>(
        private val defaultValue: T,
        private val stateSaveOption: StateSaveOption
    ) : PropertyDelegateProvider<ViewModel, ObservableProperty<T>> {
        private var distinct: Boolean = false
        private var afterSet: AfterSet<T>? = null
        private var beforeSet: BeforeSet<T>? = null
        private var validate: Validate<T>? = null

        /**
         * Sets [ObservableProperty.distinct] to `true` and returns that instance.
         */
        public fun distinct(): Provider<T> = apply { distinct = true }

        /**
         * Sets [ObservableProperty.beforeSet] to a given function and returns that instance.
         */
        public fun beforeSet(action: BeforeSet<T>): Provider<T> = apply { beforeSet = action }

        /**
         * Sets [ObservableProperty.validate] to a given function and returns that instance.
         */
        public fun validate(action: Validate<T>): Provider<T> = apply { validate = action }

        /**
         * Sets [ObservableProperty.afterSet] to a given function and returns that instance.
         */
        public fun afterSet(action: AfterSet<T>): Provider<T> = apply { afterSet = action }

        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>): ObservableProperty<T> = ObservableProperty(
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
 * Creates a new BindableProperty instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
public inline fun <reified T> ViewModel.observable(
    defaultValue: T,
    stateSaveOption: StateSaveOption? = null
): ObservableProperty.Provider<T> = ObservableProperty.Provider(defaultValue, when (this) {
    is StateSavingViewModel -> when (stateSaveOption) {
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
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
public inline fun <reified T> ViewModel.observable(
    stateSaveOption: StateSaveOption? = null
): ObservableProperty.Provider<T?> = observable(null, stateSaveOption)
