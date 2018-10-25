package de.trbnb.mvvmbase.bindableproperty

import androidx.databinding.BaseObservable
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
class BindableShortProperty<R : ViewModel> (
        private var fieldId: Int?,
        defaultValue: Short
) : BindablePropertyBase() {

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
    internal var beforeSet: (R.(Short, Short) -> Unit)? = null

    /**
     * Gets or sets a function that will validate a newly set value.
     * The first parameter is the old value and the second parameter is the new value.
     * The returned value will be the new stored value.
     *
     * If this function is null validation will not happen and the new value will simply be set.
     */
    internal var validate: (R.(Short, Short) -> Short)? = null

    /**
     * Gets or sets a function that will be invoked if a new value was set and
     * [BaseObservable.notifyPropertyChanged] was invoked.
     * The first parameter is the old value and the second parameter is the new value.
     */
    internal var afterSet: (R.(Short) -> Unit)? = null

    operator fun getValue(thisRef: R, property: KProperty<*>) = value

    operator fun setValue(thisRef: R, property: KProperty<*>, value: Short) {
        if (fieldId == null) {
            fieldId = resolveFieldId(property)
        }

        if (distinct && this.value == value) {
            return
        }

        beforeSet?.invoke(thisRef, this.value, value)
        this.value = validate?.invoke(thisRef, this.value, value) ?: value
        thisRef.notifyPropertyChanged(fieldId ?: BR._all)
        afterSet?.invoke(thisRef, this.value)
    }
}

/**
 * Creates a new [BindableShortProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 */
fun <R : ViewModel> R.bindableShort(defaultValue: Short = 0, fieldId: Int? = null): BindableShortProperty<R> {
    return BindableShortProperty(fieldId, defaultValue)
}

/**
 * Sets [BindableShortProperty.beforeSet] of a [BindableShortProperty] instance to a given function and
 * returns that instance.
 */
fun <R : ViewModel> BindableShortProperty<R>.beforeSet(action: R.(Short, Short) -> Unit) = apply { beforeSet = action }

/**
 * Sets [BindableShortProperty.validate] of a [BindableShortProperty] instance to a given function and
 * returns that instance.
 */
fun <R : ViewModel> BindableShortProperty<R>.validate(action: R.(Short, Short) -> Short) = apply { validate = action }

/**
 * Sets [BindableShortProperty.afterSet] of a [BindableShortProperty] instance to a given function and
 * returns that instance.
 */
fun <R : ViewModel> BindableShortProperty<R>.afterSet(action: R.(Short) -> Unit) = apply { afterSet = action }

