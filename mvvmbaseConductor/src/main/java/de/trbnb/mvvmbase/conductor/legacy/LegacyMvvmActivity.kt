package de.trbnb.mvvmbase.conductor.legacy

import androidx.databinding.ViewDataBinding

/**
 * Legacy version of the [de.trbnb.mvvmbase.conductor.MvvmController] to ease migration to new library version.
 * Will be removed in a future version.
 */
@Deprecated(
    message = "Migrate to MvvmController",
    replaceWith = ReplaceWith("de.trbnb.mvvmbase.MvvmController<VM>"),
    level = DeprecationLevel.WARNING
)
typealias LegacyMvvmController<VM> = LegacyMvvmBindingController<VM, ViewDataBinding>