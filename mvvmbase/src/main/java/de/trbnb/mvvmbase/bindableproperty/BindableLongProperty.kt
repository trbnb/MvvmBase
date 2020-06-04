package de.trbnb.mvvmbase.bindableproperty

import androidx.databinding.BaseObservable
import de.trbnb.mvvmbase.BR
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
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
class BindableLongProperty(
    viewModel: ViewModel,
    private var fieldId: Int?,
    defaultValue: Long,
    private val stateSaveOption: StateSaveOption
) : BindablePropertyBase() {
    /**
     * Gets or sets the stored value.
     */
    private var value: Long = when {
        stateSaveOption is StateSaveOption.Manual && viewModel is StateSavingViewModel && stateSaveOption.key in viewModel.savedStateHandle -> {
            viewModel.savedStateHandle[stateSaveOption.key] ?: defaultValue
        }
        else -> defaultValue
    }

    /**
     * The key that will be used to save the state of the property.
     */
    private var stateSavingKey: String? = (stateSaveOption as? StateSaveOption.Manual)?.key

    /**
     * Gets or sets a function that will be invoked if a new value is about to be set.
     * The first parameter is the old value and the second parameter is the new value.
     *
     * This function will not be invoked if [BindablePropertyBase.distinct] is true and the new value
     * is equal to the old value.
     */
    internal var beforeSet: ((old: Long, new: Long) -> Unit)? = null

    /**
     * Gets or sets a function that will validate a newly set value.
     * The first parameter is the old value and the second parameter is the new value.
     * The returned value will be the new stored value.
     *
     * If this function is null validation will not happen and the new value will simply be set.
     */
    internal var validate: ((old: Long, new: Long) -> Long)? = null

    /**
     * Gets or sets a function that will be invoked if a new value was set and
     * [BaseObservable.notifyPropertyChanged] was invoked.
     * The first parameter is the old value and the second parameter is the new value.
     */
    internal var afterSet: ((new: Long) -> Unit)? = null

    /**
     * @see [kotlin.properties.ReadWriteProperty.getValue]
     */
    operator fun getValue(thisRef: ViewModel, property: KProperty<*>): Long {
        detectStateSavingKey(thisRef, property)
        return value
    }

    /**
     * @see [kotlin.properties.ReadWriteProperty.setValue]
     */
    operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: Long) {
        detectStateSavingKey(thisRef, property)

        if (fieldId == null) {
            fieldId = property.resolveFieldId()
        }

        if (distinct && this.value == value) {
            return
        }

        beforeSet?.invoke(this.value, value)
        this.value = validate?.invoke(this.value, value) ?: value
        thisRef.notifyPropertyChanged(fieldId ?: BR._all)
        if (thisRef is StateSavingViewModel) {
            stateSavingKey?.let { thisRef.savedStateHandle[it] = this.value }
        }
        afterSet?.invoke(this.value)
    }

    private fun detectStateSavingKey(thisRef: ViewModel, property: KProperty<*>) {
        if (stateSaveOption is StateSaveOption.Automatic && stateSavingKey == null && thisRef is StateSavingViewModel) {
            val newStateSavingKey = property.name.also { this.stateSavingKey = it }
            if (newStateSavingKey in thisRef.savedStateHandle) {
                this.value = thisRef.savedStateHandle[newStateSavingKey] ?: return
            }
        }
    }
}

/**
 * Creates a new [BindableLongProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
fun ViewModel.bindableLong(
    defaultValue: Long = 0,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = StateSaveOption.Automatic
) = BindableLongProperty(this, fieldId, defaultValue, when (this) {
    is StateSavingViewModel -> stateSaveOption
    else -> StateSaveOption.None
})

/**
 * Sets [BindableLongProperty.beforeSet] of a [BindableLongProperty] instance to a given function and
 * returns that instance.
 */
fun BindableLongProperty.beforeSet(action: (old: Long, new: Long) -> Unit) = apply { beforeSet = action }

/**
 * Sets [BindableLongProperty.validate] of a [BindableLongProperty] instance to a given function and
 * returns that instance.
 */
fun BindableLongProperty.validate(action: (old: Long, new: Long) -> Long) = apply { validate = action }

/**
 * Sets [BindableLongProperty.afterSet] of a [BindableLongProperty] instance to a given function and
 * returns that instance.
 */
fun BindableLongProperty.afterSet(action: (new: Long) -> Unit) = apply { afterSet = action }
