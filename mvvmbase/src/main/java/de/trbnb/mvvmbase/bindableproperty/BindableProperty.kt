package de.trbnb.mvvmbase.bindableproperty

import androidx.databinding.BaseObservable
import de.trbnb.mvvmbase.BR
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.utils.resolveFieldId
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Delegate property that invokes [BaseObservable.notifyPropertyChanged] after a value is set.
 * The getter is not affected.
 *
 * @param T Type of the stored value.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param defaultValue Value that will be used at start.
 */
class BindableProperty<T> (
        private var fieldId: Int?,
        defaultValue: T
) : BindablePropertyBase(), ReadWriteProperty<ViewModel, T> {

    companion object {
        /**
         * Initializes the automatic field ID detection by providing the class inside BR.java.
         */
        @Deprecated(
            message = "Use MvvmBase.init() instead",
            replaceWith = ReplaceWith("MvvmBase.init(brClass)", "de.trbnb.mvvmbase.MvvmBase"),
            level = DeprecationLevel.WARNING
        )
        fun init(brClass: Class<*>) {
            MvvmBase.init(brClass)
        }


        /**
         * Initializes the automatic field ID detection by providing the class inside BR.java.
         */
        @Deprecated(
            message = "Use MvvmBase.init() instead",
            replaceWith = ReplaceWith("MvvmBase.init<BR>()", "de.trbnb.mvvmbase.MvvmBase"),
            level = DeprecationLevel.WARNING
        )
        inline fun <reified BR> init() = MvvmBase.init<BR>()
    }

    /**
     * Gets or sets the stored value.
     */
    private var value = defaultValue

    /**
     * Gets or sets a function that will be invoked if a new value is about to be set.
     * The first parameter is the old value and the second parameter is the new value.
     *
     * This function will not be invoked if [BindableProperty.distinct] is true and the new value
     * is equal to the old value.
     */
    internal var beforeSet: ((old: T, new: T) -> Unit)? = null

    /**
     * Gets or sets a function that will validate a newly set value.
     * The first parameter is the old value and the second parameter is the new value.
     * The returned value will be the new stored value.
     *
     * If this function is null validation will not happen and the new value will simply be set.
     */
    internal var validate: ((old: T, new: T) -> T)? = null

    /**
     * Gets or sets a function that will be invoked if a new value was set and
     * [BaseObservable.notifyPropertyChanged] was invoked.
     * The first parameter is the old value and the second parameter is the new value.
     */
    internal var afterSet: ((new: T) -> Unit)? = null

    override fun getValue(thisRef: ViewModel, property: KProperty<*>) = value

    override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: T) {
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
 * Creates a new BindableProperty instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 */
inline fun <reified T> ViewModel.bindable(defaultValue: T, fieldId: Int? = null): BindableProperty<T> {
    return BindableProperty(fieldId, defaultValue)
}

/**
 * Creates a new BindableProperty instance with `null` as default value.
 *
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 */
inline fun <reified T> ViewModel.bindable(fieldId: Int? = null): BindableProperty<T?> {
    return bindable(null, fieldId)
}

/**
 * Sets [BindableProperty.beforeSet] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <T> BindableProperty<T>.beforeSet(action: (old: T, new: T) -> Unit): BindableProperty<T> {
    return apply { beforeSet = action }
}

/**
 * Sets [BindableProperty.validate] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <T> BindableProperty<T>.validate(action: (old: T, new: T) -> T) = apply { validate = action }

/**
 * Sets [BindableProperty.afterSet] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <T> BindableProperty<T>.afterSet(action: (new: T) -> Unit) = apply { afterSet = action }
