package de.trbnb.mvvmbase.bindableproperty

import android.util.Log
import de.trbnb.mvvmbase.BR
import de.trbnb.mvvmbase.MvvmBase
import kotlin.reflect.KProperty

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

    /**
     * Finds the field ID of the given property.
     */
    protected fun resolveFieldId(property: KProperty<*>): Int {
        val brClass = MvvmBase.brClass ?: return BR._all

        val checkedPropertyName = property.brFieldName()

        Log.d("BindableProperty", "$checkedPropertyName dectected")

        return try {
            brClass.getField(checkedPropertyName).getInt(null)
        } catch (e: NoSuchFieldException) {
            Log.d("BindableProperty", "Automatic field ID detection failed for ${property.name}. Defaulting to BR._all...")
            BR._all
        }
    }
}

/**
 * Sets [BindablePropertyBase.distinct] of a [BindablePropertyBase] instance to `true` and returns that
 * instance.
 */
fun <B : BindablePropertyBase> B.distinct() = apply { setDistinct(true) }
