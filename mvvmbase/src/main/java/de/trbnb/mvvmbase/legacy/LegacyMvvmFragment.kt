package de.trbnb.mvvmbase.legacy

import androidx.databinding.ViewDataBinding

/**
 * Legacy version of the [de.trbnb.mvvmbase.MvvmFragment] to ease migration to new library version.
 * Will be removed in a future version.
 */
@Deprecated(
    message = "Migrate to MvvmFragment",
    replaceWith = ReplaceWith("de.trbnb.mvvmbase.MvvmFragment<VM>"),
    level = DeprecationLevel.WARNING
)
typealias LegacyMvvmFragment<VM> = LegacyMvvmBindingFragment<VM, ViewDataBinding>