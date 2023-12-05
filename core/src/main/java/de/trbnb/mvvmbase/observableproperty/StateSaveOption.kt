package de.trbnb.mvvmbase.observableproperty

import kotlin.reflect.KProperty

/**
 * Represents the options for state saving mechanisms that can be used by BindableProperties.
 */
public sealed class StateSaveOption {
    /**
     * The state of a property should not be saved.
     */
    public object None : StateSaveOption()

    /**
     * The state of a property should be saved and the key for it should be figured out automatically.
     */
    public object Automatic : StateSaveOption()

    /**
     * The state of a property should be saved and the key will be [key].
     */
    public class Manual(internal val key: String) : StateSaveOption()
}

/**
 * Resolves a key for [androidx.lifecycle.SavedStateHandle].
 */
public fun StateSaveOption.resolveKey(property: KProperty<*>): String? = when (this) {
    StateSaveOption.Automatic -> property.name
    is StateSaveOption.Manual -> key
    StateSaveOption.None -> null
}
