package de.trbnb.mvvmbase

import de.trbnb.mvvmbase.bindableproperty.StateSaveOption

/**
 * Object for containing library configurations.
 */
object MvvmBase {
    /**
     * Data binding bindable field IDs.
     *
     * @see init
     */
    private var brFieldIds: Map<String, Int> = emptyMap()

    internal var defaultStateSaveOption: StateSaveOption = StateSaveOption.Automatic

    /**
     * Initializes the automatic field ID detection by providing the class inside BR.java.
     */
    fun init(brClass: Class<*>): MvvmBase {
        retrieveFieldIds(brClass)
        return this
    }

    /**
     * Initializes the automatic field ID detection by providing the class inside BR.java.
     */
    inline fun <reified BR> init(): MvvmBase {
        return init(BR::class.java)
    }

    fun autoInit(): MvvmBase {
        return init<BR>()
    }

    /**
     * Sets the default [StateSaveOption] that will be used for bindable properties in [de.trbnb.mvvmbase.savedstate.StateSavingViewModel].
     */
    fun defaultStateSaveOption(stateSaveOption: StateSaveOption) = apply {
        defaultStateSaveOption = stateSaveOption
    }

    /**
     * Get data binding field ID for given property name.
     *
     * @see init
     */
    fun lookupFieldIdByName(name: String): Int? {
        return brFieldIds[name]
    }

    private fun retrieveFieldIds(brClass: Class<*>) {
        brFieldIds = brClass.fields.asSequence()
            .filter { it.type == Int::class.java }
            .map { it.name to it.getInt(null) }
            .toMap()
    }
}
