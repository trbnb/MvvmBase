package de.trbnb.mvvmbase.rxjava2

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.bindableproperty.AfterSet
import de.trbnb.mvvmbase.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.bindableproperty.Validate
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Base class for RxKotlin related BindableProperties.
 */
open class RxBindablePropertyBase<T> protected constructor(
    private val viewModel: ViewModel,
    defaultValue: T,
    private val propertyName: String,
    distinct: Boolean,
    afterSet: AfterSet<T>?,
    beforeSet: BeforeSet<T>?,
    validate: Validate<T>?
) : BindablePropertyBase<T>(distinct, afterSet, beforeSet, validate), ReadOnlyProperty<Any?, T> {
    protected var value: T = defaultValue
        set(value) {
            if (distinct && value === field) return

            val oldValue = field
            beforeSet?.invoke(oldValue, value)
            field = when (val validate = validate) {
                null -> value
                else -> validate(oldValue, value)
            }

            viewModel.notifyPropertyChanged(propertyName)
            afterSet?.invoke(oldValue, field)
        }

    final override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}
