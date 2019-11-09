package de.trbnb.mvvmbase.bindableproperty

/**
 * Base class for all BindableProperty implementations.
 */
abstract class BindablePropertyBase {
    /**
     * Gets or sets whether the setter should check if a new value is not equal to the old value.
     * If true and a value is about to be set that is equal to the old one the setter will do nothing.
     */
    protected var distinct: Boolean = false

    /**
     * Helping function to allow setting [distinct] via `internal`.
     */
    internal fun setDistinct(distinct: Boolean) {
        this.distinct = distinct
    }
}

/**
 * Sets [BindablePropertyBase.distinct] of a [BindablePropertyBase] instance to `true` and returns that
 * instance.
 */
fun <B : BindablePropertyBase> B.distinct() = apply { setDistinct(true) }
