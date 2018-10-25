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
class BindableDoubleProperty<R : ViewModel> (
        private var fieldId: Int?,
        defaultValue: Double
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
    internal var beforeSet: (R.(Double, Double) -> Unit)? = null

    /**
     * Gets or sets a function that will validate a newly set value.
     * The first parameter is the old value and the second parameter is the new value.
     * The returned value will be the new stored value.
     *
     * If this function is null validation will not happen and the new value will simply be set.
     */
    internal var validate: (R.(Double, Double) -> Double)? = null

    /**
     * Gets or sets a function that will be invoked if a new value was set and
     * [BaseObservable.notifyPropertyChanged] was invoked.
     * The first parameter is the old value and the second parameter is the new value.
     */
    internal var afterSet: (R.(Double) -> Unit)? = null

    operator fun getValue(thisRef: R, property: KProperty<*>) = value

    operator fun setValue(thisRef: R, property: KProperty<*>, value: Double) {
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
 * Creates a new [BindableDoubleProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 */
fun <R : ViewModel> R.bindableDouble(defaultValue: Double = 0.0, fieldId: Int? = null): BindableDoubleProperty<R> {
    return BindableDoubleProperty(fieldId, defaultValue)
}

/**
 * Sets [BindableDoubleProperty.beforeSet] of a [BindableDoubleProperty] instance to a given function and
 * returns that instance.
 */
fun <R : ViewModel> BindableDoubleProperty<R>.beforeSet(action: R.(Double, Double) -> Unit) = apply { beforeSet = action }

/**
 * Sets [BindableDoubleProperty.validate] of a [BindableDoubleProperty] instance to a given function and
 * returns that instance.
 */
fun <R : ViewModel> BindableDoubleProperty<R>.validate(action: R.(Double, Double) -> Double) = apply { validate = action }

/**
 * Sets [BindableDoubleProperty.afterSet] of a [BindableDoubleProperty] instance to a given function and
 * returns that instance.
 */
fun <R : ViewModel> BindableDoubleProperty<R>.afterSet(action: R.(Double) -> Unit) = apply { afterSet = action }

