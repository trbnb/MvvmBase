package de.trbnb.mvvmbase.conductor.legacy

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.conductor.MvvmBindingController
import javax.inject.Provider

/**
 * Legacy version of the [MvvmBindingController] to ease migration to new library version.
 * Will be removed in a future version.
 */
@Deprecated(
    message = "Migrate to MvvmBindingController",
    replaceWith = ReplaceWith("de.trbnb.mvvmbase.MvvmBindingController<VM, B>"),
    level = DeprecationLevel.WARNING
)
abstract class LegacyMvvmBindingController<VM, B> : MvvmBindingController<VM, B>()
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel, B : ViewDataBinding {
    abstract val viewModelProvider: Provider<VM>

    override val defaultViewModelProviderFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}

