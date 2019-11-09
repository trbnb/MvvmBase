package de.trbnb.mvvmbase.bindableproperty

/**
 * Represents the options for state saving mechanisms that can be used by BindableProperties.
 */
sealed class StateSaveOption {
    /**
     * The state of a property should not be saved.
     */
    object None : StateSaveOption()

    /**
     * The state of a property should be saved and the key for it should be figured out automatically.
     */
    object Automatic : StateSaveOption()

    /**
     * The state of a property should be saved and the key will be [key].
     */
    class Manual(internal val key: String) : StateSaveOption()
}
