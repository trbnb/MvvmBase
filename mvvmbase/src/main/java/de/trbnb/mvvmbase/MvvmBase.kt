package de.trbnb.mvvmbase

/**
 * Object for containing library configurations.
 */
object MvvmBase {
    /**
     * Data binding bindable fields class.
     *
     * @see init
     */
    internal var brClass: Class<*>? = null
        private set

    /**
     * Initializes the automatic field ID detection by providing the class inside BR.java.
     */
    fun init(brClass: Class<*>) {
        this.brClass = brClass
    }

    /**
     * Initializes the automatic field ID detection by providing the class inside BR.java.
     */
    inline fun <reified BR> init() {
        init(BR::class.java)
    }
}
