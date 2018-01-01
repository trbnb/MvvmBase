package de.trbnb.mvvmbase

import android.databinding.BaseObservable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Delegate property that invokes [BaseObservable.notifyPropertyChanged] after a value is set.
 * The getter is not affected.
 *
 * @param T Type of the stored value.
 * @param fieldId Value of field ID inside BR.java.
 * @param defaultValue Value that will be used at start.
 */
class BindableProperty<T>(
        private val fieldId: Int,
        defaultValue: T
) : ReadWriteProperty<BaseObservable, T> {

    /**
     * Gets or sets the stored value.
     */
    private var value = defaultValue

    /**
     * Gets or sets whether the setter should check if a new value is not equal to the old value.
     * If true and a value is about to be set that is equal to the old one the setter will do nothing.
     */
    internal var distinct: Boolean = false

    /**
     * Gets or sets a function that will be invoked if a new value is about to be set.
     * The first parameter is the old value and the second parameter is the new value.
     *
     * This function will not be invoked if [BindableProperty.distinct] is true and the new value
     * is equal to the old value.
     */
    internal var beforeSet: ((T, T) -> Unit)? = null

    /**
     * Gets or sets a function that will validate a newly set value.
     * The first parameter is the old value and the second parameter is the new value.
     * The returned value will be the new stored value.
     *
     * If this function is null validation will not happen and the new value will simply be set.
     */
    internal var validate: ((T, T) -> T)? = null

    /**
     * Gets or sets a function that will be invoked if a new value was set and
     * [BaseObservable.notifyPropertyChanged] was invoked.
     * The first parameter is the old value and the second parameter is the new value.
     */
    internal var afterSet: ((T) -> Unit)? = null

    override fun getValue(thisRef: BaseObservable, property: KProperty<*>) = value

    override fun setValue(thisRef: BaseObservable, property: KProperty<*>, value: T) {
        if (distinct && this.value == value) {
            return
        }

        beforeSet?.invoke(this.value, value)
        this.value = validate?.invoke(this.value, value) ?: value
        thisRef.notifyPropertyChanged(fieldId)
        afterSet?.invoke(value)
    }
}

fun <T> bindable(fieldId: Int, defaultValue: T): BindableProperty<T> {
    return BindableProperty(fieldId, defaultValue)
}

/**
 * Sets [BindableProperty.distinct] of a [BindableProperty] instance to `true` and returns that
 * instance.
 */
fun <T> BindableProperty<T>.distinct() = apply { distinct = true }

/**
 * Sets [BindableProperty.beforeSet] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <T> BindableProperty<T>.beforeSet(action: (T, T) -> Unit) = apply { beforeSet = action }

/**
 * Sets [BindableProperty.validate] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <T> BindableProperty<T>.validate(action: (T, T) -> T) = apply { validate = action }

/**
 * Sets [BindableProperty.afterSet] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <T> BindableProperty<T>.afterSet(action: (T) -> Unit) = apply { afterSet = action }