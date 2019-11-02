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
class BindableByteProperty(
    private var fieldId: Int?,
    defaultValue: Byte,
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
    internal var beforeSet: ((old:Byte, new: Byte) -> Unit)? = null

    /**
     * Gets or sets a function that will validate a newly set value.
     * The first parameter is the old value and the second parameter is the new value.
     * The returned value will be the new stored value.
     *
     * If this function is null validation will not happen and the new value will simply be set.
     */
    internal var validate: ((old:Byte, new: Byte) -> Byte)? = null

    /**
     * Gets or sets a function that will be invoked if a new value was set and
     * [BaseObservable.notifyPropertyChanged] was invoked.
     * The first parameter is the old value and the second parameter is the new value.
     */
    internal var afterSet: ((new: Byte) -> Unit)? = null

    operator fun getValue(thisRef: ViewModel, property: KProperty<*>): Byte {
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

    operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: Byte) {
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
 * Creates a new [BindableByteProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param stateSaveOption Specifies if the state of the property should be saved with a [androidx.lifecycle.SavedStateHandle] and with which key.
 */
fun ViewModel.bindableByte(
    defaultValue: Byte = 0,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption = StateSaveOption.Automatic
) = BindableByteProperty(fieldId, defaultValue, stateSaveOption)

/**
 * Sets [BindableByteProperty.beforeSet] of a [BindableByteProperty] instance to a given function and
 * returns that instance.
 */
fun BindableByteProperty.beforeSet(action: (old: Byte, new: Byte) -> Unit) = apply { beforeSet = action }

/**
 * Sets [BindableByteProperty.validate] of a [BindableByteProperty] instance to a given function and
 * returns that instance.
 */
fun BindableByteProperty.validate(action: (old: Byte, new: Byte) -> Byte) = apply { validate = action }

/**
 * Sets [BindableByteProperty.afterSet] of a [BindableByteProperty] instance to a given function and
 * returns that instance.
 */
fun BindableByteProperty.afterSet(action: (Byte) -> Unit) = apply { afterSet = action }

