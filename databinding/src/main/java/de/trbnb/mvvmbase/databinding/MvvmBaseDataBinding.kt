package de.trbnb.mvvmbase.databinding

import androidx.annotation.VisibleForTesting
import de.trbnb.mvvmbase.MvvmBase

/**
 * Object for containing library configurations.
 */
internal object MvvmBaseDataBinding {
    private var brFieldIds: Map<String, Int> = emptyMap()

    /**
     * Get data binding field ID for given property name.
     *
     * @see initDataBinding
     */
    fun lookupFieldIdByName(name: String): Int? = brFieldIds[name]

    internal fun retrieveFieldIds(brClass: Class<*>) {
        brFieldIds = brClass.fields.asSequence()
            .filter { it.type == Int::class.java }
            .map { it.name to it.getInt(null) }
            .toMap()
    }
}

/**
 * Initializes the library with the BR class from itself (which will be expanded by the databinding compiler and so will contain every field id).
 */
fun MvvmBase.initDataBinding() = apply {
    MvvmBaseDataBinding.retrieveFieldIds(BR::class.java)
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun MvvmBase.resetDataBinding() = apply {
    MvvmBaseDataBinding.retrieveFieldIds(Unit::class.java)
}
