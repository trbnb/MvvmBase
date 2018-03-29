package de.trbnb.mvvmbase

import android.databinding.BaseObservable
import android.util.Log
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
class BindableProperty<R : BaseObservable, T>(
        private var fieldId: Int?,
        defaultValue: T
) : ReadWriteProperty<R, T> {

    companion object {
        private var brClass: Class<*>? = null

        fun init(brClass: Class<*>) {
            this.brClass = brClass
        }

        inline fun <reified BR> init() = init(BR::class.java)
    }

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
    internal var beforeSet: (R.(T, T) -> Unit)? = null

    /**
     * Gets or sets a function that will validate a newly set value.
     * The first parameter is the old value and the second parameter is the new value.
     * The returned value will be the new stored value.
     *
     * If this function is null validation will not happen and the new value will simply be set.
     */
    internal var validate: (R.(T, T) -> T)? = null

    /**
     * Gets or sets a function that will be invoked if a new value was set and
     * [BaseObservable.notifyPropertyChanged] was invoked.
     * The first parameter is the old value and the second parameter is the new value.
     */
    internal var afterSet: (R.(T) -> Unit)? = null

    override fun getValue(thisRef: R, property: KProperty<*>) = value

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        if (fieldId == null) {
            fieldId = resolveFieldId(property.name)
        }

        if (distinct && this.value == value) {
            return
        }

        beforeSet?.invoke(thisRef, this.value, value)
        this.value = validate?.invoke(thisRef, this.value, value) ?: value
        thisRef.notifyPropertyChanged(fieldId ?: BR._all)
        afterSet?.invoke(thisRef, value)
    }

    private fun resolveFieldId(propertyName: String): Int {
        val brClass = brClass ?: return BR._all

        val checkedPropertyName = when (propertyName.startsWith("is", ignoreCase = true)) {
            false -> propertyName
            true -> "${propertyName[2].toLowerCase()}${propertyName.substring(3)}"
        }

        Log.d("BindableProperty", "$checkedPropertyName dectected")

        return try {
            brClass.getField(checkedPropertyName).getInt(null)
        } catch (e: NoSuchFieldException) {
            Log.d("BindableProperty", "Automatic field ID detection failed for $propertyName. Defaulting to BR._all...")
            BR._all
        }
    }
}

fun <R : BaseObservable, T> R.bindable(defaultValue: T, fieldId: Int? = null): BindableProperty<R, T> {
    return BindableProperty(fieldId, defaultValue)
}

/**
 * Sets [BindableProperty.distinct] of a [BindableProperty] instance to `true` and returns that
 * instance.
 */
fun <R : BaseObservable, T> BindableProperty<R, T>.distinct() = apply { distinct = true }

/**
 * Sets [BindableProperty.beforeSet] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <R : BaseObservable, T> BindableProperty<R, T>.beforeSet(action: R.(T, T) -> Unit) = apply { beforeSet = action }

/**
 * Sets [BindableProperty.validate] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <R : BaseObservable, T> BindableProperty<R, T>.validate(action: R.(T, T) -> T) = apply { validate = action }

/**
 * Sets [BindableProperty.afterSet] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <R : BaseObservable, T> BindableProperty<R, T>.afterSet(action: R.(T) -> Unit) = apply { afterSet = action }
