package de.trbnb.mvvmbase.bindableproperty

import android.databinding.BaseObservable
import de.trbnb.mvvmbase.BR
import de.trbnb.mvvmbase.ViewModel
import kotlin.reflect.KProperty

/**
 * Delegate property that invokes [BaseObservable.notifyPropertyChanged] after a value is set.
 * The getter is not affected.
 *
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param defaultValue Value that will be used at start.
 */
class BindableByteProperty(private var fieldId: Int?, defaultValue: Byte) : BindablePropertyBase() {
    override val isBoolean = false

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

    operator fun getValue(thisRef: ViewModel, property: KProperty<*>) = value

    operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: Byte) {
        if (fieldId == null) {
            fieldId = resolveFieldId(property)
        }

        if (distinct && this.value == value) {
            return
        }

        beforeSet?.invoke(this.value, value)
        this.value = validate?.invoke(this.value, value) ?: value
        thisRef.notifyPropertyChanged(fieldId ?: BR._all)
        afterSet?.invoke(this.value)
    }
}

/**
 * Creates a new [BindableByteProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 */
fun ViewModel.bindableByte(defaultValue: Byte = 0, fieldId: Int? = null): BindableByteProperty {
    return BindableByteProperty(fieldId, defaultValue)
}

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

