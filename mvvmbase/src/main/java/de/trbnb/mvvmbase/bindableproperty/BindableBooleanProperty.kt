package de.trbnb.mvvmbase.bindableproperty

import androidx.databinding.BaseObservable
import de.trbnb.mvvmbase.BR
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.utils.resolveFieldId
import kotlin.reflect.KProperty

/**
 * Delegate property that invokes [BaseObservable.notifyPropertyChanged] after a value is set.
 * The getter is not affected.
 *
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param defaultValue Value that will be used at start.
 */
class BindableBooleanProperty(private var fieldId: Int?, defaultValue: Boolean) : BindablePropertyBase() {
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
    internal var beforeSet: ((old: Boolean, new: Boolean) -> Unit)? = null

    /**
     * Gets or sets a function that will validate a newly set value.
     * The first parameter is the old value and the second parameter is the new value.
     * The returned value will be the new stored value.
     *
     * If this function is null validation will not happen and the new value will simply be set.
     */
    internal var validate: ((old: Boolean, new: Boolean) -> Boolean)? = null

    /**
     * Gets or sets a function that will be invoked if a new value was set and
     * [BaseObservable.notifyPropertyChanged] was invoked.
     * The first parameter is the old value and the second parameter is the new value.
     */
    internal var afterSet: ((new: Boolean) -> Unit)? = null

    operator fun getValue(thisRef: ViewModel, property: KProperty<*>) = value

    operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: Boolean) {
        if (fieldId == null) {
            fieldId = property.resolveFieldId()
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
 * Creates a new [BindableBooleanProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 */
fun ViewModel.bindableBoolean(defaultValue: Boolean = false, fieldId: Int? = null): BindableBooleanProperty {
    return BindableBooleanProperty(fieldId, defaultValue)
}

/**
 * Sets [BindableBooleanProperty.beforeSet] of a [BindableBooleanProperty] instance to a given function and
 * returns that instance.
 */
fun BindableBooleanProperty.beforeSet(action: (old: Boolean, new: Boolean) -> Unit) = apply { beforeSet = action }

/**
 * Sets [BindableBooleanProperty.validate] of a [BindableBooleanProperty] instance to a given function and
 * returns that instance.
 */
fun BindableBooleanProperty.validate(action: (old: Boolean, new: Boolean) -> Boolean) = apply { validate = action }

/**
 * Sets [BindableBooleanProperty.afterSet] of a [BindableBooleanProperty] instance to a given function and
 * returns that instance.
 */
fun BindableBooleanProperty.afterSet(action: (Boolean) -> Unit) = apply { afterSet = action }

