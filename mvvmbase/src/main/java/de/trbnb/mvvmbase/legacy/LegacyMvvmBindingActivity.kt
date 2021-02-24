package de.trbnb.mvvmbase.legacy

import androidx.databinding.ViewDataBinding
import de.trbnb.mvvmbase.MvvmBindingActivity
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.utils.findGenericSuperclass
import de.trbnb.mvvmbase.viewmodel.asViewModelProviderFactory
import javax.inject.Provider

/**
 * Legacy version of the [MvvmBindingActivity] to ease migration to new library version.
 * Will be removed in a future version.
 */
@Deprecated(
    message = "Migrate to MvvmBindingActivity",
    replaceWith = ReplaceWith("de.trbnb.mvvmbase.MvvmBindingActivity<VM, B>"),
    level = DeprecationLevel.WARNING
)
abstract class LegacyMvvmBindingActivity<VM, B> : MvvmBindingActivity<VM, B>()
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel, B : ViewDataBinding {
    /**
     * Specifies how to instantiate a new [VM].
     */
    abstract val viewModelProvider: Provider<VM>

    @Suppress("UNCHECKED_CAST")
    override val viewModelClass: Class<VM>
        get() = findGenericSuperclass<LegacyMvvmBindingActivity<VM, B>>()
            ?.actualTypeArguments
            ?.firstOrNull() as? Class<VM>
            ?: throw IllegalStateException("viewModelClass does not equal Class<VM>")

    override fun getDefaultViewModelProviderFactory() = viewModelProvider.asViewModelProviderFactory()
}
