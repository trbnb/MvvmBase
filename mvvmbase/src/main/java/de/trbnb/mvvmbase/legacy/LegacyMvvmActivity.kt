package de.trbnb.mvvmbase.legacy

import androidx.databinding.ViewDataBinding

/**
 * Legacy version of the [de.trbnb.mvvmbase.MvvmActivity] to ease migration to new library version.
 * Will be removed in a future version.
 */
@Deprecated(
    message = "Migrate to MvvmActivity",
    replaceWith = ReplaceWith("de.trbnb.mvvmbase.MvvmActivity<VM>"),
    level = DeprecationLevel.WARNING
)
typealias LegacyMvvmActivity<VM> = LegacyMvvmBindingActivity<VM, ViewDataBinding>
