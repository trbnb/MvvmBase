package de.trbnb.mvvmbase.bindableproperty

import kotlin.reflect.KProperty

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

/**
 * Resolves a key for [androidx.lifecycle.SavedStateHandle].
 */
fun StateSaveOption.resolveKey(property: KProperty<*>): String? = when (this) {
    StateSaveOption.Automatic -> property.name
    is StateSaveOption.Manual -> key
    StateSaveOption.None -> null
}
