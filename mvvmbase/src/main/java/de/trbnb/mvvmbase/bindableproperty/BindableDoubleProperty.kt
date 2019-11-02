package de.trbnb.mvvmbase.bindableproperty

import androidx.databinding.BaseObservable
import de.trbnb.mvvmbase.BR
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.utils.brFieldName
import de.trbnb.mvvmbase.utils.resolveFieldId
import kotlin.reflect.KProperty

/**
 * Delegate property that invokes [BaseObservable.notifyPropertyChanged] and saves state via the ViewModels
 * [androidx.lifecycle.SavedStateHandle].
 *
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param defaultValue Value that will be used at start.
 * @param stateSaveOption Specifies if the state of the property should be saved with a [androidx.lifecycle.SavedStateHandle] and with which key.
 */
class BindableDoubleProperty(
    private var fieldId: Int?,
    defaultValue: Double,
    private val stateSaveOption: StateSaveOption
) : BindablePropertyBase() {
    /**
     * The key that will be used to save the state of the property.
     */
    private var savedStateKey: String? = (stateSaveOption as? StateSaveOption.Manual)?.key

    /**
     * Gets or sets the stored value.
     */
    private var value = defaultValue

    /**
     * Gets or sets a function that will be invoked if a new value is about to be set.
     * The first parameter is the old value and the second parameter is the new value.
     *
     * This function will not be invoked if [BindablePropertyBase.distinct] is true and the new value
     * is equal to the old value.
     */
    internal var beforeSet: ((old: Double, new: Double) -> Unit)? = null

    /**
     * Gets or sets a function that will validate a newly set value.
     * The first parameter is the old value and the second parameter is the new value.
     * The returned value will be the new stored value.
     *
     * If this function is null validation will not happen and the new value will simply be set.
     */
    internal var validate: ((old: Double, new: Double) -> Double)? = null

    /**
     * Gets or sets a function that will be invoked if a new value was set and
     * [BaseObservable.notifyPropertyChanged] was invoked.
     * The first parameter is the old value and the second parameter is the new value.
     */
    internal var afterSet: ((new: Double) -> Unit)? = null

    operator fun getValue(thisRef: ViewModel, property: KProperty<*>): Double {
        if (stateSaveOption is StateSaveOption.Automatic && savedStateKey == null) {
            val savedStateKey = property.brFieldName().also { this.savedStateKey = it }

            thisRef.withSavedStateHandle { savedStateHandle ->
                if (savedStateKey in savedStateHandle) {
                    this.value = savedStateHandle.get(savedStateKey) ?: return@withSavedStateHandle
                }
            }
        }
        return value
    }

    operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: Double) {
        if (fieldId == null) {
            fieldId = property.resolveFieldId()
        }

        if (distinct && this.value == value) {
            return
        }

        beforeSet?.invoke(this.value, value)
        this.value = validate?.invoke(this.value, value) ?: value
        thisRef.notifyPropertyChanged(fieldId ?: BR._all)
        if (stateSaveOption !is StateSaveOption.None) {
            savedStateKey?.let { thisRef.savedStateHandle?.set(it, this.value) }
        }
        afterSet?.invoke(this.value)
    }
}

/**
 * Creates a new [BindableDoubleProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param stateSaveOption Specifies if the state of the property should be saved with a [androidx.lifecycle.SavedStateHandle] and with which key.
 */
fun ViewModel.bindableDouble(
    defaultValue: Double = 0.0,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = StateSaveOption.Automatic
) = BindableDoubleProperty(fieldId, defaultValue, stateSaveOption)

/**
 * Sets [BindableDoubleProperty.beforeSet] of a [BindableDoubleProperty] instance to a given function and
 * returns that instance.
 */
fun BindableDoubleProperty.beforeSet(action: (old: Double, new: Double) -> Unit) = apply { beforeSet = action }

/**
 * Sets [BindableDoubleProperty.validate] of a [BindableDoubleProperty] instance to a given function and
 * returns that instance.
 */
fun BindableDoubleProperty.validate(action: (old: Double, new: Double) -> Double) = apply { validate = action }

/**
 * Sets [BindableDoubleProperty.afterSet] of a [BindableDoubleProperty] instance to a given function and
 * returns that instance.
 */
fun BindableDoubleProperty.afterSet(action: (new: Double) -> Unit) = apply { afterSet = action }

