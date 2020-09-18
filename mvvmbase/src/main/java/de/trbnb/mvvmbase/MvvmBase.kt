package de.trbnb.mvvmbase

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

    /**
     * Initializes the automatic field ID detection by providing the class inside BR.java.
     */
    fun init(brClass: Class<*>) {
        retrieveFieldIds(brClass)
    }

    /**
     * Initializes the automatic field ID detection by providing the class inside BR.java.
     */
    inline fun <reified BR> init() {
        init(BR::class.java)
    }

    fun autoInit() {
        init<BR>()
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
