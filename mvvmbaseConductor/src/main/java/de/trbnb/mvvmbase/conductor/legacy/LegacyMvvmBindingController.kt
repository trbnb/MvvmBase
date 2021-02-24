package de.trbnb.mvvmbase.conductor.legacy

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.conductor.MvvmBindingController
import de.trbnb.mvvmbase.utils.findGenericSuperclass
import de.trbnb.mvvmbase.viewmodel.asViewModelProviderFactory
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
    /**
     * Specifies how to instantiate a new [VM].
     */
    abstract val viewModelProvider: Provider<VM>

    @Suppress("UNCHECKED_CAST")
    override val viewModelClass: Class<VM>
        get() = findGenericSuperclass<LegacyMvvmBindingController<VM, B>>()
            ?.actualTypeArguments
            ?.firstOrNull() as? Class<VM>
            ?: throw IllegalStateException("viewModelClass does not equal Class<VM>")

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory = viewModelProvider.asViewModelProviderFactory()
}
