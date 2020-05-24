package de.trbnb.mvvmbase.bindableproperty

import androidx.databinding.BaseObservable
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.savedstate.StateSavingViewModel
import de.trbnb.mvvmbase.utils.resolveFieldId
import de.trbnb.mvvmbase.utils.savingStateInBindableSupports
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Delegate property that invokes [BaseObservable.notifyPropertyChanged] and saves state
 * via [StateSavingViewModel.savedStateHandle].
 *
 * @param T Type of the stored value.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param defaultValue Value that will be used at start if value can not be restored from [StateSavingViewModel.savedStateHandle].
 * @param distinct See [BindablePropertyBase.distinct].
 * @param stateSavingKey Specifies with which key the value will be state-saved. No state-saving if `null`.
 * @param afterSet Gets or sets a function that will be invoked if a new value was set and
 * [androidx.databinding.BaseObservable.notifyPropertyChanged] was invoked.
 * The first parameter is the old value and the second parameter is the new value.
 * @param validate Gets or sets a function that will validate a newly set value.
 * The first parameter is the old value and the second parameter is the new value.
 * The returned value will be the new stored value. If this function is null validation will not happen and the new value will simply be set.
 * @param beforeSet Gets or sets a function that will be invoked if a new value is about to be set.
 * The first parameter is the old value and the second parameter is the new value.
 * This function will not be invoked if [BindableProperty.distinct] is true and the new value is equal to the old value.
 */
class BindableProperty<T>(
    viewModel: ViewModel,
    private val fieldId: Int,
    defaultValue: T,
    distinct: Boolean,
    private val stateSavingKey: String?,
    private val afterSet: ((new: T) -> Unit)?,
    private val validate: ((old: T, new: T) -> T)?,
    private val beforeSet: ((old: T, new: T) -> Unit)?
) : BindablePropertyBase(distinct), ReadWriteProperty<ViewModel, T> {
    /**
     * Gets or sets the stored value.
     */
    @Suppress("RemoveExplicitTypeArguments", "UNCHECKED_CAST")
    private var value: T = when {
        stateSavingKey != null && viewModel is StateSavingViewModel && stateSavingKey in viewModel.savedStateHandle -> {
            viewModel.savedStateHandle.get<T>(stateSavingKey) as T
        }
        else -> defaultValue
    }

    override operator fun getValue(thisRef: ViewModel, property: KProperty<*>): T = value

    override operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: T) {
        if (distinct && this.value == value) {
            return
        }

        beforeSet?.invoke(this.value, value)
        this.value = when (val validate = validate) {
            null -> value
            else -> validate(this.value, value)
        }

        thisRef.notifyPropertyChanged(fieldId)
        if (thisRef is StateSavingViewModel && stateSavingKey != null) {
            thisRef.savedStateHandle[stateSavingKey] = this.value
        }
        afterSet?.invoke(this.value)
    }

    class Provider<T>(
        private val fieldId: Int? = null,
        private val defaultValue: T,
        private val stateSaveOption: StateSaveOption
    ): BindablePropertyBase.Provider() {
        internal var afterSet: ((new: T) -> Unit)? = null
        internal var validate: ((old: T, new: T) -> T)? = null
        internal var beforeSet: ((old: T, new: T) -> Unit)? = null

        operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>): BindableProperty<T> {
            val stateSavingKey = when (stateSaveOption) {
                StateSaveOption.Automatic -> property.name
                is StateSaveOption.Manual -> stateSaveOption.key
                StateSaveOption.None -> null
            }

            return BindableProperty(
                viewModel = thisRef,
                fieldId = fieldId ?: property.resolveFieldId(),
                defaultValue = defaultValue,
                stateSavingKey = stateSavingKey,
                distinct = distinct,
                afterSet = afterSet,
                validate = validate,
                beforeSet = beforeSet
            )
        }
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
    is StateSavingViewModel -> when (stateSaveOption) {
        null -> when (savingStateInBindableSupports<T>()) {
            true -> StateSaveOption.Automatic
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

/**
 * Sets [BindableProperty.beforeSet] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <T> BindableProperty.Provider<T>.beforeSet(action: (old: T, new: T) -> Unit): BindableProperty.Provider<T> = apply { beforeSet = action }

/**
 * Sets [BindableProperty.validate] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <T> BindableProperty.Provider<T>.validate(action: (old: T, new: T) -> T): BindableProperty.Provider<T> = apply { validate = action }

/**
 * Sets [BindableProperty.afterSet] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <T> BindableProperty.Provider<T>.afterSet(action: (new: T) -> Unit): BindableProperty.Provider<T> = apply { afterSet = action }

/**
 * Sets [BindableProperty.afterSet] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <T> BindableProperty.Provider<T>.distinct(): BindableProperty.Provider<T> = apply { distinct = true }
