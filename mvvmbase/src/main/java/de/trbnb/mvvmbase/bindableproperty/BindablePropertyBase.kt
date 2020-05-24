package de.trbnb.mvvmbase.bindableproperty

import de.trbnb.mvvmbase.ViewModel
import kotlin.reflect.KProperty

typealias BeforeSet<T> = (old: T, new: T) -> Unit
typealias Validate<T> = (old: T, new: T) -> T
typealias AfterSet<T> = (new: T) -> Unit

/**
 * Base class for all BindableProperty implementations.
 *
 * @param distinct Gets or sets whether the setter should check if a new value is not equal to the old value.
 * If true and a value is about to be set that is equal to the old one the setter will do nothing.
 *
 * @param afterSet Gets or sets a function that will be invoked if a new value was set and
 * [androidx.databinding.BaseObservable.notifyPropertyChanged] was invoked.
 * The first parameter is the old value and the second parameter is the new value.
 *
 * @param validate Gets or sets a function that will validate a newly set value.
 * The first parameter is the old value and the second parameter is the new value.
 * The returned value will be the new stored value.
 * If this function is null validation will not happen and the new value will simply be set.
 *
 * @param beforeSet Gets or sets a function that will be invoked if a new value is about to be set.
 * The first parameter is the old value and the second parameter is the new value.
 * This function will not be invoked if [BindablePropertyBase.distinct] is true and the new value is equal to the old value.
 */
abstract class BindablePropertyBase<T>(
    val distinct: Boolean,
    val afterSet: AfterSet<T>?,
    val beforeSet: BeforeSet<T>?,
    val validate: Validate<T>?
) {
    abstract class Provider<T> {
        internal var distinct: Boolean = false
        internal var afterSet: AfterSet<T>? = null
        internal var beforeSet: BeforeSet<T>? = null
        internal var validate: Validate<T>? = null

        abstract operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>): BindablePropertyBase<T>
    }
}

/**
 * Sets [BindablePropertyBase.distinct] to `true` and returns that instance.
 */
fun <T, P : BindablePropertyBase.Provider<T>> P.distinct(): P = apply { distinct = true }

/**
 * Sets [BindablePropertyBase.beforeSet] to a given function and returns that instance.
 */
fun <T, P : BindablePropertyBase.Provider<T>> P.beforeSet(action: (old: T, new: T) -> Unit): P = apply { beforeSet = action }

/**
 * Sets [BindablePropertyBase.validate] to a given function and returns that instance.
 */
fun <T, P : BindablePropertyBase.Provider<T>> P.validate(action: (old: T, new: T) -> T): P = apply { validate = action }

/**
 * Sets [BindablePropertyBase.afterSet] to a given function and returns that instance.
 */
fun <T, P : BindablePropertyBase.Provider<T>> P.afterSet(action: (new: T) -> Unit): P = apply { afterSet = action }
