package de.trbnb.mvvmbase.rxjava3

import de.trbnb.mvvmbase.databinding.ViewModel
import de.trbnb.mvvmbase.databinding.bindableproperty.AfterSet
import de.trbnb.mvvmbase.databinding.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.databinding.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.databinding.bindableproperty.Validate
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Base class for RxKotlin related BindableProperties.
 */
public open class RxBindablePropertyBase<T> protected constructor(
    private val viewModel: ViewModel,
    defaultValue: T,
    private val fieldId: Int,
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

            viewModel.notifyPropertyChanged(fieldId)
            afterSet?.invoke(oldValue, field)
        }

    final override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}
