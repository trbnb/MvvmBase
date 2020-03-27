package de.trbnb.mvvmbase.rxjava2

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.utils.resolveFieldId
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Base class for RxKotlin related BindableProperties.
 */
open class RxBindablePropertyBase<T> protected constructor(
    private val viewModel: ViewModel,
    defaultValue: T,
    private var fieldId: Int?
) : BindablePropertyBase(), ReadOnlyProperty<Any?, T> {
    internal var afterSet: ((T) -> Unit)? = null

    protected var value: T = defaultValue
        set(value) {
            if (distinct && value == field) return

            field = value
            fieldId?.let { viewModel.notifyPropertyChanged(it) }
            afterSet?.invoke(value)
        }

    final override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (fieldId == null) {
            fieldId = property.resolveFieldId()
        }

        return value
    }
}

/**
 * Sets [RxBindablePropertyBase.afterSet] of a [RxBindablePropertyBase] instance to a given function and
 * returns that instance.
 */
fun <T, P : RxBindablePropertyBase<T>> P.afterSet(action: (T) -> Unit): P = apply {
    afterSet = action
}
