package de.trbnb.mvvmbase.bindableproperty

/**
 * Base class for all BindableProperty implementations.
 *
 * @param distinct Gets or sets whether the setter should check if a new value is not equal to the old value.
 * If true and a value is about to be set that is equal to the old one the setter will do nothing.
 */
abstract class BindablePropertyBase(var distinct: Boolean = false) {
    abstract class Provider {
        internal var distinct: Boolean = false
    }
}

/**
 * Sets [BindablePropertyBase.distinct] of a [BindablePropertyBase] instance to `true` and returns that
 * instance.
 */
fun <B : BindablePropertyBase> B.distinct() = apply { distinct = true }
fun <P : BindablePropertyBase.Provider> P.distinct(): P = apply { distinct = true }
